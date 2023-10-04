package dev.scaraz.mars.app.administration.telegram.ticket;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import dev.scaraz.mars.app.administration.config.client.WitelApi;
import dev.scaraz.mars.app.administration.config.client.WitelClient;
import dev.scaraz.mars.app.administration.config.telegram.AuthorizeduserInterceptor;
import dev.scaraz.mars.app.administration.domain.cache.FormTicketRegistrationCache;
import dev.scaraz.mars.app.administration.domain.extern.Issue;
import dev.scaraz.mars.app.administration.domain.extern.IssueParam;
import dev.scaraz.mars.app.administration.repository.cache.FormTicketRegistrationCacheRepo;
import dev.scaraz.mars.app.administration.service.app.ConfigService;
import dev.scaraz.mars.app.administration.service.extern.IssueService;
import dev.scaraz.mars.app.administration.telegram.ReplyKeyboardConstant;
import dev.scaraz.mars.app.administration.web.dto.UserAccount;
import dev.scaraz.mars.common.domain.general.TicketBotForm;
import dev.scaraz.mars.common.domain.request.CreateTicketDTO;
import dev.scaraz.mars.common.domain.response.TicketShortDTO;
import dev.scaraz.mars.common.exception.telegram.TgInvalidFormError;
import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.exception.web.InternalServerException;
import dev.scaraz.mars.common.tools.Translator;
import dev.scaraz.mars.common.tools.enums.TcSource;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.common.utils.RealmConstant;
import dev.scaraz.mars.telegram.config.TelegramContextHolder;
import dev.scaraz.mars.telegram.service.TelegramBotService;
import dev.scaraz.mars.telegram.util.TelegramUtil;
import dev.scaraz.mars.telegram.util.enums.ChatSource;
import feign.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class TicketRegistrationFlow {

    private final Gson GSON = new Gson();

    private final ConfigService configService;
    private final ObjectMapper objectMapper;

    private final IssueService issueService;
    private final FormTicketRegistrationCacheRepo ticketRegistrationCacheRepo;
    private final TicketFormRegistration ticketFormRegistration;

    private final TelegramBotService botService;

    private final WitelClient witelClient;

    public SendMessage instant_start(long chatId) {
        if (TelegramContextHolder.getChatSource() != ChatSource.PRIVATE) {
            return SendMessage.builder()
                    .chatId(chatId)
                    .text(Translator.tr("telegram.chat.source.private.only"))
                    .build();
        }

        UserAccount account = (UserAccount) TelegramContextHolder.getAttribute(AuthorizeduserInterceptor.ATTRIBUTE);
        boolean allowedToCreate = account.getRealmRoles().contains(RealmConstant.Permission.TICKET_CREATE.getKey());
        if (!allowedToCreate)
            throw new BadRequestException("Maaf akun anda tidak diperbolehkan membuat tiket");

        try {
            Message message = botService.getClient().execute(SendMessage.builder()
                    .parseMode(ParseMode.MARKDOWNV2)
                    .chatId(chatId)
                    .text(TelegramUtil.esc(
                            Translator.tr("telegram.ticket.registration.issue", account.getName())
                    ))
                    .replyMarkup(InlineKeyboardMarkup.builder()
                            .keyboard(issueService.getKeyboards(account.getWitel()))
                            .build())
                    .build());

            ticketRegistrationCacheRepo.save(FormTicketRegistrationCache.builder()
                    .id(message.getMessageId())
                    .state(FormTicketRegistrationCache.State.ISSUE)
                    .ttl(5)
                    .build());
        }
        catch (TelegramApiException e) {
            throw InternalServerException.args(e.getMessage());
        }

        return null;
    }

    public void instant_answerIssue(long messageId, String issueCode) throws TelegramApiException {
        Message message = botService.getClient().execute(SendMessage.builder()
                .chatId(TelegramContextHolder.getUserId())
                .text(Translator.tr("telegram.ticket.registration.network"))
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboardRow(ReplyKeyboardConstant.NETWORK_AGGREEMENT)
                        .build())
                .build());

        String code = issueCode.replace(AppConstants.Telegram.REPORT_ISSUE, "");
        ticketRegistrationCacheRepo.replace(messageId, b -> b
                .id(message.getMessageId())
                .state(FormTicketRegistrationCache.State.NETWORK)
                .issueCode(code)
                .ttl(5)
        );
    }

    public void instant_answerNetwork(long messageId, boolean agree) throws TelegramApiException {
        if (agree) {
            ticketRegistrationCacheRepo.deleteById(messageId);
            botService.getClient().execute(SendMessage.builder()
                    .chatId(TelegramContextHolder.getUserId())
                    .parseMode(ParseMode.MARKDOWNV2)
                    .text(TelegramUtil.esc(Translator.tr("telegram.ticket.registration.network.agree")))
                    .build());
        }
        else {
            FormTicketRegistrationCache cache = ticketRegistrationCacheRepo.findById(messageId).orElseThrow();
            Issue issue = issueService.findByCode(cache.getIssueCode());
            String desc = issue.getDescription() == null ? "-" : issue.getDescription();
            Message message = botService.getClient().execute(SendMessage.builder()
                    .chatId(TelegramContextHolder.getUserId())
                    .parseMode(ParseMode.MARKDOWNV2)
                    .text(TelegramUtil.esc(Translator.tr("telegram.ticket.registration.param", desc)))
                    .replyMarkup(InlineKeyboardMarkup.builder()
                            .keyboardRow(ReplyKeyboardConstant.PARAM_AGGREEMENT)
                            .build())
                    .build());

            ticketRegistrationCacheRepo.replace(messageId, b -> b
                    .id(message.getMessageId())
                    .state(FormTicketRegistrationCache.State.PARAM)
                    .ttl(5)
            );
        }
    }

    public void instant_answerParam(long messageId, boolean agree) throws TelegramApiException {
        if (agree) {
            ticketRegistrationCacheRepo.deleteById(messageId);
            botService.getClient().execute(SendMessage.builder()
                    .chatId(TelegramContextHolder.getUserId())
                    .parseMode(ParseMode.MARKDOWNV2)
                    .text(TelegramUtil.esc(Translator.tr("telegram.ticket.registration.param.agree")))
                    .build());
        }
        else {
            FormTicketRegistrationCache cache = ticketRegistrationCacheRepo.findById(messageId).orElseThrow();
            Issue issue = issueService.findByCode(cache.getIssueCode());

            String name = Objects.requireNonNullElse(issue.getName(), issue.getCode());
            String additionalField = issue.getParams().isEmpty() ? null :
                    issue.getParams().stream()
                            .sorted(Comparator.comparing(IssueParam::getType))
                            .map(this::generateIssueParam)
                            .collect(Collectors.joining("\n")) + "\n";

            Message message = botService.getClient().execute(SendMessage.builder()
                    .chatId(TelegramContextHolder.getChatId())
                    .parseMode(ParseMode.MARKDOWNV2)
                    .text(TelegramUtil.esc(
                            String.format("*[%s]* Mohon input request order sesuai format:", name),
                            "",
                            "Tiket NOSSA: _(required)_",
                            "No Service: _(required)_",
                            "Witel: _(opt)_",
                            "STO: _(opt)_",
                            additionalField,
                            "",
                            "_Untuk *Witel/STO* jika tidak diisi, akan menyesuaikan dengan *Witel/STO* user yg menginput.",
                            "*Balas pesan, dengan mengreply balon chat ini*._"
                    ))
                    .build());

            ticketRegistrationCacheRepo.replace(messageId, b -> b
                    .id(message.getMessageId())
                    .state(FormTicketRegistrationCache.State.FORM)
                    .ttl(5)
            );
        }
    }

    public void instant_end(long messageId,
                            String text,
                            @Nullable Collection<PhotoSize> captures,
                            @Nullable Document document
    ) throws TelegramApiException {
        FormTicketRegistrationCache cache = ticketRegistrationCacheRepo.findById(messageId).orElseThrow();

        Issue issue = issueService.findByCode(cache.getIssueCode());
        TcSource source = TelegramContextHolder.getChatSource() == ChatSource.PRIVATE ?
                TcSource.PRIVATE :
                TelegramContextHolder.getChatSource() == ChatSource.GROUP ?
                        TcSource.GROUP : TcSource.OTHER;

        UserAccount account = (UserAccount) TelegramContextHolder.getAttribute(AuthorizeduserInterceptor.ATTRIBUTE);

        TicketBotForm form = ticketFormRegistration.parseTicketRegistration(text).toBuilder()
                .product(issue.getProduct())
                .issueId(issue.getId())
                .source(source)
//                .senderId(account.getTelegramId())
//                .senderName(account.getName())
                .build();

        for (IssueParam param : issue.getParams()) {
            switch (param.getType()) {
                case CAPTURE:
                    if (param.isRequired() && (captures == null || captures.isEmpty())) {
                        throw new TgInvalidFormError(
                                Objects.requireNonNullElse(param.getDisplay(), "Capture/Attachment"),
                                "Mohon upload capture yang diperlukan");
                    }
                    break;
                case FILE:
                    if (param.isRequired() && document == null) {
                        throw new TgInvalidFormError(
                                Objects.requireNonNullElse(param.getDisplay(), "Document/Attachment"),
                                "Mohon upload dokumen/file yang diperlukan");
                    }
                    break;
                case NOTE:
                    ticketFormRegistration.parseTicketNote(form, text, List.of(
                            Objects.requireNonNullElse(param.getDisplay(), "Deskripsi")
                    ));
                    if (param.isRequired() && form.getNote() == null) {
                        throw new TgInvalidFormError(
                                Objects.requireNonNullElse(param.getDisplay(), "Worklog/Deskripsi"),
                                "Mohon menyediakan deskripsi yang diperlukan");
                    }
                    break;
            }
        }

        WitelApi api = witelClient.get(account.getWitel());
        String json = GSON.toJson(new CreateTicketDTO(form));

        try (Response response = api.createTicket(json)) {
            if (response.status() == 200) {
                try (JsonParser parser = objectMapper.createParser(response.body().asInputStream())) {
                    TicketShortDTO ticket = parser.readValueAs(TicketShortDTO.class);

                    botService.getClient().execute(SendMessage.builder()
                            .chatId(account.getTelegramId())
                            .parseMode(ParseMode.MARKDOWNV2)
                            .text(TelegramUtil.esc(
                                    String.format(
                                            "Request telah tercatat dan diterima dengan no order *%s*", ticket.getNo()),
                                    "",
                                    "Menunggu request diproses."
                            ))
                            .build());

                    ticketRegistrationCacheRepo.deleteById(messageId);
                }
                catch (IOException ex) {
                    throw InternalServerException.args(ex.getMessage());
                }
            }
            else {
            }
        }
    }

    private String generateIssueParam(IssueParam param) {
        String name;
        switch (param.getType()) {
            case CAPTURE:
                name = Objects.requireNonNullElse(param.getDisplay(), "Sertakan Capture");
                break;
            case NOTE:
                name = Objects.requireNonNullElse(param.getDisplay(), "Deskripsi");
                break;
            case FILE:
                name = Objects.requireNonNullElse(param.getDisplay(), "Dokumen");
                break;
            default:
                throw InternalServerException.args("Invalid Param Type");
        }
        name += ":";

        if (param.isRequired()) name += " _(required)_";
        else name += " _(opt)_";
        return name;
    }
}

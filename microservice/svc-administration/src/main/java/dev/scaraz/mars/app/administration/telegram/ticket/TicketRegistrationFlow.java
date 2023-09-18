package dev.scaraz.mars.app.administration.telegram.ticket;

import dev.scaraz.mars.app.administration.config.telegram.AuthorizeduserInterceptor;
import dev.scaraz.mars.app.administration.domain.cache.FormTicketRegistrationCache;
import dev.scaraz.mars.app.administration.domain.extern.Issue;
import dev.scaraz.mars.app.administration.domain.extern.IssueParam;
import dev.scaraz.mars.app.administration.repository.cache.FormTicketRegistrationCacheRepo;
import dev.scaraz.mars.app.administration.service.extern.IssueService;
import dev.scaraz.mars.app.administration.web.dto.UserAccount;
import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.exception.web.InternalServerException;
import dev.scaraz.mars.common.tools.Translator;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.common.utils.RealmConstant;
import dev.scaraz.mars.telegram.config.TelegramContextHolder;
import dev.scaraz.mars.telegram.service.TelegramBotService;
import dev.scaraz.mars.telegram.util.TelegramUtil;
import dev.scaraz.mars.telegram.util.enums.ChatSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketRegistrationFlow {


    private final IssueService issueService;

    private final FormTicketRegistrationCacheRepo ticketRegistrationCacheRepo;
    private final TelegramBotService telegramBotService;

    public SendMessage start(long chatId) throws TelegramApiException {
        if (TelegramContextHolder.getChatSource() != ChatSource.PRIVATE) {
            return SendMessage.builder()
                    .chatId(chatId)
                    .text("Maaf, command ini hanya bisa ditampilkan melalui private chat")
                    .build();
        }

        UserAccount user = getUser();
        List<String> roles = user.getRoles(RealmConstant.CLIENT_MARS_WITEL_RESOURCE);
        if (!roles.contains(RealmConstant.Permission.TICKET_CREATE.getKey()))
            throw new BadRequestException("Job Desk anda tidak diperbolehkan membuat tiket");

        Message message = telegramBotService.getClient().execute(SendMessage.builder()
                .parseMode(ParseMode.MARKDOWNV2)
                .chatId(user.getTelegramId())
                .text(TelegramUtil.esc(
                        String.format("Halo *%s*", user.getName()),
                        "Silahkan pilih menu sesuai kendala:"
                ))
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboard(issueService.getKeyboards(user.getWitel()))
                        .build())
                .build());

        ticketRegistrationCacheRepo.save(FormTicketRegistrationCache.builder()
                .id(message.getMessageId())
                .state(FormTicketRegistrationCache.State.ISSUE)
                .ttl(10)
                .build());
        return null;
    }

    public void answerIssue(long messageId, String data) throws TelegramApiException {
        String issueCode = data.replace(AppConstants.Telegram.REPORT_ISSUE, "");
        Issue issue = issueService.findByCode(issueCode);

        UserAccount user = getUser();
        Message message = telegramBotService.getClient().execute(SendMessage.builder()
                .chatId(user.getTelegramId())
                .text("Mohon pastikan apakah jaringan terindikasi LOS dan/atau Unspec ?")
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboardRow(List.of(
                                InlineKeyboardButton.builder()
                                        .text(Translator.tr("Ya"))
                                        .callbackData(AppConstants.Telegram.REG_TICKET_NETWORK_AGREE)
                                        .build(),
                                InlineKeyboardButton.builder()
                                        .text(Translator.tr("Tidak"))
                                        .callbackData(AppConstants.Telegram.REG_TICKET_NETWORK_DISAGREE)
                                        .build()
                        ))
                        .build())
                .build());

        ticketRegistrationCacheRepo.update(messageId, b -> b
                .id(message.getMessageId())
                .state(FormTicketRegistrationCache.State.NETWORK)
                .issueCode(issue.getCode())
                .ttl(10)
        );
    }

    public SendMessage answerNetwork(long messageId, boolean agree) throws TelegramApiException {
        UserAccount user = getUser();
        if (agree) {
            return SendMessage.builder()
                    .chatId(user.getTelegramId())
                    .parseMode(ParseMode.MARKDOWNV2)
                    .text(TelegramUtil.esc(
                            "Maaf, *Mars* hanya bisa memproses tiket dengan jaringan yang tidak *LOS* dan/atau *Unspec*.",
                            "Silahkan melakukan pengecekan dan perbaikan fisik."
                    ))
                    .build();
        }

        FormTicketRegistrationCache cache = getCache(messageId);
        Issue issue = issueService.findByCode(cache.getIssueCode());

        String desc = issue.getDescription() == null ? "-" : issue.getDescription();
        Integer paramMessageId = telegramBotService.getClient().execute(SendMessage.builder()
                        .chatId(user.getTelegramId())
                        .parseMode(ParseMode.MARKDOWNV2)
                        .text(TelegramUtil.esc(
                                "Pastikan parameter berikut sudah sesuai:",
                                "",
                                desc,
                                "",
                                "Apakah kendala sudah teratasi ?"
                        ))
                        .replyMarkup(InlineKeyboardMarkup.builder()
                                .keyboardRow(List.of(
                                        InlineKeyboardButton.builder()
                                                .text(Translator.tr("Sudah"))
                                                .callbackData(AppConstants.Telegram.REG_TICKET_PARAM_AGREE)
                                                .build(),
                                        InlineKeyboardButton.builder()
                                                .text(Translator.tr("Belum"))
                                                .callbackData(AppConstants.Telegram.REG_TICKET_PARAM_DISAGREE)
                                                .build()
                                ))
                                .build())
                        .build())
                .getMessageId();

        ticketRegistrationCacheRepo.update(messageId, b -> b
                .id(paramMessageId)
                .state(FormTicketRegistrationCache.State.PARAM)
                .ttl(10));

        return null;
    }

    public SendMessage answerParam(long messageId, boolean agree) throws TelegramApiException {
        UserAccount user = getUser();
        if (agree) {
            return SendMessage.builder()
                    .chatId(user.getTelegramId())
                    .parseMode(ParseMode.MARKDOWNV2)
                    .text(TelegramUtil.esc(
                            "Nice!, Mohon selalu pastikan parameter tersebut kedepannya ya!",
                            "Terima kasih telah menggunakan *MARS*"
                    ))
                    .build();
        }

        FormTicketRegistrationCache cache = getCache(messageId);
        Issue issue = issueService.findByCode(cache.getIssueCode());

        String displayName = StringUtils.isBlank(issue.getName()) ?
                issue.getCode() : issue.getName();

        String additionalField = issue.getParams().isEmpty() ? null :
                issue.getParams().stream()
                        .sorted(Comparator.comparing(IssueParam::getType))
                        .map(this::generateIssueParam)
                        .collect(Collectors.joining("\n")) + "\n";

        Integer paramMessageId = telegramBotService.getClient().execute(SendMessage.builder()
                        .chatId(user.getTelegramId())
                        .parseMode(ParseMode.MARKDOWNV2)
                        .text(TelegramUtil.esc(
                                String.format("*[%s]* Mohon input request order sesuai format:", displayName),
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
                        .build())
                .getMessageId();

        ticketRegistrationCacheRepo.update(messageId, b -> b
                .id(paramMessageId)
                .state(FormTicketRegistrationCache.State.FORM)
        );

        return null;
    }

    public SendMessage end(long messageId, String text) {
        return null;
    }

    private UserAccount getUser() {
        return (UserAccount) TelegramContextHolder.getAttribute(AuthorizeduserInterceptor.ATTRIBUTE);
    }

    private FormTicketRegistrationCache getCache(long messageId) {
        return ticketRegistrationCacheRepo.findById(messageId).orElseThrow();
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

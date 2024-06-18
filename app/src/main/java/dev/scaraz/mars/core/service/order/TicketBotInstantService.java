package dev.scaraz.mars.core.service.order;

import dev.scaraz.mars.common.domain.general.TicketBotForm;
import dev.scaraz.mars.common.exception.telegram.TgInvalidFormError;
import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.exception.web.InternalServerException;
import dev.scaraz.mars.common.exception.web.NotFoundException;
import dev.scaraz.mars.common.tools.enums.TcSource;
import dev.scaraz.mars.common.utils.AuthorityConstant;
import dev.scaraz.mars.common.utils.ConfigConstants;
import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.domain.symptom.Issue;
import dev.scaraz.mars.core.domain.symptom.IssueParam;
import dev.scaraz.mars.core.domain.order.Ticket;
import dev.scaraz.mars.core.domain.order.TicketConfirm;
import dev.scaraz.mars.core.query.AccountQueryService;
import dev.scaraz.mars.core.query.IssueQueryService;
import dev.scaraz.mars.core.service.ConfigService;
import dev.scaraz.mars.core.service.NotifierService;
import dev.scaraz.mars.security.MarsUserContext;
import dev.scaraz.mars.telegram.config.TelegramContextHolder;
import dev.scaraz.mars.telegram.service.TelegramBotService;
import dev.scaraz.mars.telegram.util.TelegramUtil;
import dev.scaraz.mars.telegram.util.enums.ChatSource;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketBotInstantService {

    //    private final AppConfigService appConfigService;
    private final ApplicationContext applicationContext;
    private final ConfigService configService;
    private final TelegramBotService botService;

    private final AccountQueryService accountQueryService;

    private final IssueQueryService issueQueryService;


    private final TicketBotService ticketBotService;
    private final TicketFormService ticketFormService;

    private final ConfirmService confirmService;



    // Instant Form
    public SendMessage instantForm_start(Long chatId) throws TelegramApiException {
        if (TelegramContextHolder.getChatSource() != ChatSource.PRIVATE) {
            return SendMessage.builder()
                    .chatId(chatId)
                    .text("Maaf, menu ini hanya bisa ditampilkan melalui private chat")
                    .build();
        }

        Account account = accountQueryService.findByCurrentAccess();
        if (account.hasAnyRole(AuthorityConstant.AGENT_ROLE)) {
            boolean allowedCreate = configService.get(ConfigConstants.APP_ALLOW_AGENT_CREATE_TICKET_BOOL)
                    .getAsBoolean();

            if (!allowedCreate)
                throw new BadRequestException("Agen tidak diperbolehkan membuat tiket sendiri");
        }

        String name = account.getName();
        SendMessage toSend = SendMessage.builder()
                .parseMode(ParseMode.MARKDOWNV2)
                .chatId(chatId)
                .text(TelegramUtil.esc(
                        String.format("Halo *%s*", name),
                        "Silahkan pilih menu sesuai kendala:"
                ))
                .replyMarkup(ticketBotService.createIssueKeyboarButtons())
                .build();

        int messageId = botService.getClient().execute(toSend).getMessageId();
        confirmService.save(TicketConfirm.builder()
                .id(messageId)
                .status(TicketConfirm.INSTANT_START)
                .ttl(5)
                .build());
        return null;
    }

    public void instantForm_answerIssue(long messageId, long issueId) throws TelegramApiException {
        issueQueryService.findById(issueId)
                .orElseThrow(() -> NotFoundException.entity(Issue.class, "id", issueId));

        long userId = MarsUserContext.getTelegram();
        Message message = botService.getClient().execute(SendMessage.builder()
                .chatId(userId)
                .text("Mohon pastikan apakah jaringan terindikasi LOS dan/atau Unspec ?")
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboardRow(List.of(NotifierService.BTN_AGREE, NotifierService.BTN_DISAGREE))
                        .build())
                .build());

        confirmService.save(TicketConfirm.builder()
                .id(message.getMessageId())
                .issueId(issueId)
                .status(TicketConfirm.INSTANT_NETWORK)
                .ttl(10)
                .build());

        confirmService.deleteById(messageId);
    }

    public SendMessage instantForm_answerNetwork(long messageId, boolean agree) throws TelegramApiException {
        Long userId = MarsUserContext.getTelegram();
        if (agree) {
            return SendMessage.builder()
                    .chatId(userId)
                    .parseMode(ParseMode.MARKDOWNV2)
                    .text(TelegramUtil.esc(
                            "Maaf, *Mars* hanya bisa memproses tiket dengan jaringan yang tidak *LOS* dan/atau *Unspec*.",
                            "Silahkan melakukan pengecekan dan perbaikan fisik."
                    ))
                    .build();
        }

        TicketConfirm confirm = confirmService.findById(messageId);
        Issue issue = issueQueryService.findById(confirm.getIssueId())
                .orElseThrow();

        String desc = issue.getDescription() == null ? "-" : issue.getDescription();
        Integer paramMessageId = botService.getClient().execute(SendMessage.builder()
                        .chatId(userId)
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
                                        NotifierService.BTN_AGREE_CUSTOM("Sudah"),
                                        NotifierService.BTN_DIAGREE_CUSTOM("Belum")
                                ))
                                .build())
                        .build())
                .getMessageId();

        confirmService.save(TicketConfirm.builder()
                .id(paramMessageId)
                .issueId(issue.getId())
                .status(TicketConfirm.INSTANT_PARAM)
                .ttl(10)
                .build());
        confirmService.deleteById(messageId);
        return null;
    }

    @Transactional
    public SendMessage instantForm_answerParamRequirement(long messageId, boolean agree) throws TelegramApiException {
        if (agree) {
            return SendMessage.builder()
                    .chatId(TelegramContextHolder.getChatId())
                    .parseMode(ParseMode.MARKDOWNV2)
                    .text(TelegramUtil.esc(
                            "Nice!, Mohon selalu pastikan parameter tersebut kedepannya ya!",
                            "Terima kasih telah menggunakan *MARS*"
                    ))
                    .build();
        }

        TicketConfirm confirm = confirmService.findById(messageId);
        Issue issue = issueQueryService.findById(confirm.getIssueId())
                .orElseThrow();

        String name = Objects.requireNonNullElse(issue.getAlias(), issue.getName());
        String additionalField = issue.getParams().isEmpty() ? null :
                issue.getParams().stream()
                        .sorted(Comparator.comparing(IssueParam::getType))
                        .map(this::generateIssueParam)
                        .collect(Collectors.joining("\n")) + "\n";

        Integer paramMessageId = botService.getClient().execute(SendMessage.builder()
                        .chatId(TelegramContextHolder.getChatId())
                        .parseMode(ParseMode.MARKDOWNV2)
                        .text(TelegramUtil.esc(
                                String.format("*[%s]* Mohon input request order sesuai format:", name),
                                "",
                                "No Tiket: _(required)_",
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

        confirm.setId(paramMessageId);

        confirmService.save(TicketConfirm.builder()
                .id(paramMessageId)
                .issueId(issue.getId())
                .status(TicketConfirm.INSTANT_FORM)
                .ttl(30)
                .build());
        confirmService.deleteById(messageId);
        return null;
    }

    @Transactional
    public SendMessage instantForm_end(long messageId,
                                       String text,
                                       @Nullable Collection<PhotoSize> captures,
                                       @Nullable Document document
    ) {
        Account account = accountQueryService.findByCurrentAccess();

        TicketConfirm confirm = confirmService.findById(messageId);
        Issue issue = issueQueryService.findById(confirm.getIssueId())
                .orElseThrow();

        TcSource source =
                TelegramContextHolder.getChatSource() == ChatSource.PRIVATE ?
                        TcSource.PRIVATE :
                        TelegramContextHolder.getChatSource() == ChatSource.GROUP ?
                                TcSource.GROUP : TcSource.OTHER;

        TicketBotForm form = ticketFormService.parseTicketRegistration(text).toBuilder()
                .product(issue.getProduct())
                .issueId(issue.getId())
                .source(source)
                .senderId(account.getTg().getId())
                .senderName(account.getName())
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
                    ticketFormService.parseTicketNote(form, text, List.of(
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

        Ticket ticket = ticketBotService.registerForm(
                ticketFormService.checkInstantForm(form),
                captures,
                document
        );
        confirmService.deleteById(messageId);

        return SendMessage.builder()
                .chatId(account.getTg().getId())
                .parseMode(ParseMode.MARKDOWNV2)
                .text(TelegramUtil.esc(
                        String.format(
                                "Request telah tercatat dan diterima dengan no order *%s*", ticket.getNo()),
                        "",
                        "Menunggu request diproses."
                ))
                .build();
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

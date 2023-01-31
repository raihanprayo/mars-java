package dev.scaraz.mars.core.service.order.impl;

import dev.scaraz.mars.common.domain.request.TicketStatusFormDTO;
import dev.scaraz.mars.common.exception.web.InternalServerException;
import dev.scaraz.mars.common.exception.web.NotFoundException;
import dev.scaraz.mars.common.tools.annotation.FormDescriptor;
import dev.scaraz.mars.common.domain.general.TicketBotForm;
import dev.scaraz.mars.common.exception.telegram.TgError;
import dev.scaraz.mars.common.exception.telegram.TgInvalidFormError;
import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.common.tools.enums.TcSource;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.core.domain.order.*;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.query.IssueQueryService;
import dev.scaraz.mars.core.query.TicketQueryService;
import dev.scaraz.mars.core.service.NotifierService;
import dev.scaraz.mars.core.service.StorageService;
import dev.scaraz.mars.core.service.order.*;
import dev.scaraz.mars.core.util.SecurityUtil;
import dev.scaraz.mars.telegram.config.TelegramContextHolder;
import dev.scaraz.mars.telegram.service.TelegramBotService;
import dev.scaraz.mars.telegram.util.TelegramUtil;
import dev.scaraz.mars.telegram.util.enums.ChatSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static dev.scaraz.mars.common.utils.AppConstants.Telegram.ISSUES_BUTTON_LIST;

@Slf4j
@RequiredArgsConstructor
@Service
public class TicketBotServiceImpl implements TicketBotService {

    private final TelegramBotService botService;

    private final TicketService service;
    private final TicketFlowService flowService;
    private final TicketQueryService queryService;
    private final IssueQueryService issueQueryService;


    private final TicketFormService ticketFormService;
    private final TicketConfirmService ticketConfirmService;

    private final StorageService storageService;
    private final LogTicketService logTicketService;

    @Override
    @Transactional
    public Ticket registerForm(TicketBotForm form, @Nullable Collection<PhotoSize> photos) {
        Issue issue = issueQueryService.findById(form.getIssueId())
                .orElseThrow();

        int totalGaul = queryService.countGaul(form.getIssueId(), form.getService());

        User user = SecurityUtil.getCurrentUser();
        Ticket ticket = service.save(Ticket.builder()
                .witel(form.getWitel() == null ? user.getWitel() : form.getWitel())
                .sto(form.getSto() == null ? user.getSto() : form.getSto())
                .issue(issue)
                .incidentNo(form.getIncident())
                .serviceNo(form.getService())
                .source(form.getSource())
                .senderId(user.getTg().getId())
                .senderName(user.getName())
                .note(form.getNote())
                .gaul(totalGaul)
                .build());

        log.debug("REGISTERED NEW TICKET -- TICKET NO={} GAUL={} TYPE={}/{}",
                ticket.getNo(), totalGaul != 0,
                issue.getProduct(),
                issue.getName());

        if (photos != null && !photos.isEmpty())
            storageService.addPhotoForTicketAsync(photos, ticket);

        logTicketService.add(LogTicket.builder()
                .ticket(ticket)
                .curr(ticket.getStatus())
                .message(String.format(
                        "created ticket with gaul is %s",
                        totalGaul != 0
                ))
                .build());

        return ticket;
    }

    @Override
    public Ticket take(String ticketNo) {
        return flowService.take(ticketNo);
    }

    @Override
    @Transactional
    public void confirmedClose(
            long messageId,
            boolean closeTicket,
            @Nullable String note) {
        TicketConfirm confirmData = ticketConfirmService.findById(messageId);

        flowService.confirmClose(confirmData.getNo(), !closeTicket, TicketStatusFormDTO.builder()
                .note(note)
                .build());

        ticketConfirmService.deleteById(messageId);
    }

    @Override
    public void confirmedPending(long messageId, boolean pendingTicket) {
        TicketConfirm confirmData = ticketConfirmService.findById(messageId);
        flowService.confirmPending(confirmData.getNo(), pendingTicket, new TicketStatusFormDTO());
        ticketConfirmService.deleteById(messageId);
    }

    @Override
    public void confirmedPostPending(long messageId, @Nullable String text, @Nullable Collection<PhotoSize> photos) {
        TicketConfirm confirmData = ticketConfirmService.findById(messageId);
        TicketStatusFormDTO form = TicketStatusFormDTO.builder()
                .status(text == null ? TcStatus.CLOSED : TcStatus.REOPEN)
                .note(text)
                .build();

        if (photos != null && !photos.isEmpty()) form.setPhotos(new ArrayList<>(photos));
        flowService.confirmPostPending(confirmData.getNo(), form);
        ticketConfirmService.deleteById(messageId);
    }

    @Override
    public void validateForm(TicketBotForm form) throws TgInvalidFormError {
        // check required fields
        Map<String, FormDescriptor> descriptors = TicketBotForm.getDescriptors();

        log.info("Descriptor Keys {}", descriptors.keySet());
        for (String fieldName : descriptors.keySet()) {
            if (fieldName.equals("note")) continue;
            try {
                Field field = form.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);

                FormDescriptor formDescriptor = descriptors.get(field.getName());
                Object o = field.get(form);

                log.info("* CHECK FIELD {} -- required ? {} - alias {}", field.getName(), formDescriptor.required(), formDescriptor.alias());
                if (o == null && formDescriptor.required()) {
                    throw new TgInvalidFormError(
                            field.getName(),
                            "error.ticket.form.missing.field",
                            List.of(formDescriptor.alias()));
                }
                field.setAccessible(false);
            }
            catch (NoSuchFieldException | IllegalAccessException e) {
                throw new TgError(e);
            }
        }

        // value validations
        ticketFormService.checkFieldIncidentNo(form);
        ticketFormService.checkFieldServiceNo(form);
        ticketFormService.checkFieldIssue(form);
    }


    // Instant Form
    @Override
    public SendMessage instantForm_start(Long chatId) throws TelegramApiException {
        if (TelegramContextHolder.getChatSource() != ChatSource.PRIVATE) {
            return SendMessage.builder()
                    .chatId(chatId)
                    .text("Maaf, menu ini hanya bisa ditampilkan melalui private chat")
                    .build();
        }

        String name = SecurityUtil.getCurrentUser().getName();

        InlineKeyboardMarkup markUp = new InlineKeyboardMarkup();
        SendMessage toSend = SendMessage.builder()
                .parseMode(ParseMode.MARKDOWNV2)
                .chatId(chatId)
                .text(TelegramUtil.esc(
                        String.format("Halo *%s*", name),
                        "Silahkan pilih menu sesuai kendala:"
                ))
                .replyMarkup(markUp)
                .build();

//            Function<Product, String> textTitle = (p) -> String.format("Jenis *%s*", p);

        LinkedMultiValueMap<Product, InlineKeyboardButton> all = new LinkedMultiValueMap<>(ISSUES_BUTTON_LIST);
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        for (Product product : all.keySet()) {
            buttons.add(List.of(InlineKeyboardButton.builder()
                    .text(product.name())
                    .callbackData("DUMMY")
                    .build()));
            buttons.add(all.get(product));
        }

        markUp.setKeyboard(buttons);
        int messageId = botService.getClient().execute(toSend).getMessageId();
        ticketConfirmService.save(TicketConfirm.builder()
                .id(messageId)
                .status(TicketConfirm.INSTANT_START)
                .build());
        return null;
    }

    @Override
    public void instantForm_answerIssue(long messageId, long issueId) throws TelegramApiException {
        issueQueryService.findById(issueId)
                .orElseThrow(() -> NotFoundException.entity(Issue.class, "id", issueId));

        long userId = Objects.requireNonNull(SecurityUtil.getCurrentUser()).getTg().getId();
        Message message = botService.getClient().execute(SendMessage.builder()
                .chatId(userId)
                .text("Mohon pastikan apakah jaringan terindikasi LOS dan/atau Unspec ?")
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboardRow(List.of(NotifierService.BTN_AGREE, NotifierService.BTN_DISAGREE))
                        .build())
                .build());

        ticketConfirmService.save(TicketConfirm.builder()
                .id(message.getMessageId())
                .issueId(issueId)
                .status(TicketConfirm.INSTANT_NETWORK)
                .ttl(30)
                .build());

        ticketConfirmService.deleteById(messageId);
    }

    @Override
    public SendMessage instantForm_answerNetwork(long messageId, boolean agree) throws TelegramApiException {
        Long userId = SecurityUtil.getCurrentUser().getTg().getId();
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

        TicketConfirm confirm = ticketConfirmService.findById(messageId);
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

        ticketConfirmService.save(TicketConfirm.builder()
                .id(paramMessageId)
                .issueId(issue.getId())
                .status(TicketConfirm.INSTANT_PARAM)
                .ttl(30)
                .build());
        ticketConfirmService.deleteById(messageId);
        return null;
    }

    @Override
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

        TicketConfirm confirm = ticketConfirmService.findById(messageId);
        Issue issue = issueQueryService.findById(confirm.getIssueId())
                .orElseThrow();

        String name = Objects.requireNonNullElse(issue.getAlias(), issue.getName());
        String additionalField = issue.getParams().isEmpty() ? null :
                issue.getParams().stream()
                        .sorted(Comparator.comparing(IssueParam::getType))
                        .map(this::generateIssueParam)
                        .collect(Collectors.joining("\n")) + "\n";

        // TODO: tambah custom parameter per-issue
        Integer paramMessageId = botService.getClient().execute(SendMessage.builder()
                        .chatId(TelegramContextHolder.getChatId())
                        .parseMode(ParseMode.MARKDOWNV2)
                        .text(TelegramUtil.esc(
                                String.format("*[%s]* Mohon input request order sesuai format:", name),
                                "",
                                "Tiket NOSSA: _(opt)_",
                                "No Service: _(required)_",
                                additionalField,
                                "",
                                "_Balas pesan, dengan mengreply balon chat ini_"
                        ))
                        .build())
                .getMessageId();

        confirm.setId(paramMessageId);

        ticketConfirmService.save(TicketConfirm.builder()
                .id(paramMessageId)
                .issueId(issue.getId())
                .status(TicketConfirm.INSTANT_FORM)
                .ttl(30)
                .build());
        ticketConfirmService.deleteById(messageId);
        return null;
    }

    @Override
    @Transactional
    public SendMessage instantForm_end(long messageId, String text, @Nullable Collection<PhotoSize> captures) {
        User user = Objects.requireNonNull(SecurityUtil.getCurrentUser());

        TicketConfirm confirm = ticketConfirmService.findById(messageId);
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
                .senderId(user.getTg().getId())
                .senderName(user.getName())
                .build();

        for (IssueParam param : issue.getParams()) {
            switch (param.getType()) {
                case CAPTURE:
                    if (captures == null || captures.isEmpty()) {
                        throw new TgInvalidFormError(
                                Objects.requireNonNullElse(param.getDisplay(), "Capture/Attachment"),
                                "Mohon upload capture yang diperlukan");
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
            }
        }

        Ticket ticket = registerForm(ticketFormService.checkInstantForm(form), captures);
        ticketConfirmService.deleteById(messageId);

        return SendMessage.builder()
                .chatId(user.getTg().getId())
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
                name += ":";
                break;
            default:
                throw InternalServerException.args("Invalid Param Type");

        }

        if (param.isRequired()) name += " _(required)_";
        else name += " _(opt)_";
        return name;
    }
}

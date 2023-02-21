package dev.scaraz.mars.core.service.order.impl;

import dev.scaraz.mars.common.domain.request.TicketStatusFormDTO;
import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.exception.web.InternalServerException;
import dev.scaraz.mars.common.exception.web.NotFoundException;
import dev.scaraz.mars.common.tools.annotation.FormDescriptor;
import dev.scaraz.mars.common.domain.general.TicketBotForm;
import dev.scaraz.mars.common.exception.telegram.TgError;
import dev.scaraz.mars.common.exception.telegram.TgInvalidFormError;
import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.common.tools.enums.TcSource;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.core.domain.order.*;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.domain.view.TicketSummary;
import dev.scaraz.mars.core.query.*;
import dev.scaraz.mars.core.query.criteria.UserCriteria;
import dev.scaraz.mars.core.repository.order.LogTicketRepo;
import dev.scaraz.mars.core.service.AppConfigService;
import dev.scaraz.mars.core.service.NotifierService;
import dev.scaraz.mars.core.service.StorageService;
import dev.scaraz.mars.core.service.order.*;
import dev.scaraz.mars.core.service.order.flow.CloseFlowService;
import dev.scaraz.mars.core.service.order.flow.DispatchFlowService;
import dev.scaraz.mars.core.service.order.flow.PendingFlowService;
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
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static dev.scaraz.mars.common.utils.AppConstants.Telegram.ISSUES_BUTTON_LIST;
import static dev.scaraz.mars.common.utils.AppConstants.ZONE_LOCAL;

@Slf4j
@RequiredArgsConstructor
@Service
public class TicketBotServiceImpl implements TicketBotService {

    private final AppConfigService appConfigService;
    private final TelegramBotService botService;

    private final AgentQueryService agentQueryService;

    private final TicketService service;
    private final TicketFlowService flowService;
    private final TicketQueryService queryService;
    private final TicketSummaryQueryService summaryQueryService;
    private final LogTicketRepo logTicketRepo;

    private final UserQueryService userQueryService;
    private final IssueQueryService issueQueryService;


    private final TicketFormService ticketFormService;
    private final CloseFlowService closeFlowService;
    private final PendingFlowService pendingFlowService;
    private final DispatchFlowService dispatchFlowService;

    private final TicketConfirmService ticketConfirmService;

    private final StorageService storageService;
    private final LogTicketService logTicketService;

    @Override
    @Transactional(readOnly = true)
    public SendMessage info(String ticketNo) {
        TicketSummary tc = summaryQueryService.findByIdOrNo(ticketNo);
        Map<String, Object> infoMap = new LinkedHashMap<>();
//        infoMap.put("No", tc.getNo());
        infoMap.put("Witel", tc.getWitel());
        infoMap.put("STO", tc.getSto());
        infoMap.put("Requestor", tc.getSenderName());
        infoMap.put("Gaul", tc.isGaul() ? "Ya" : "Tidak");
        infoMap.put("No Service", tc.getServiceNo());
        infoMap.put("Tiket NOSSA", tc.getIncidentNo());
        infoMap.put("Status", tc.getStatus());

        StringBuilder content = new StringBuilder()
                .append("Informasi Tiket *")
                .append(tc.getNo())
                .append("*:\n");

        for (String k : infoMap.keySet()) {
            String value = Objects.requireNonNullElse(infoMap.get(k), "-")
                    .toString();
            content.append(k).append(": \t*")
                    .append(value)
                    .append("*\n");
        }

        content.append("\n\n")
                .append("Timeline:\n");

        DateTimeFormatter formatDateLog = DateTimeFormatter.ofPattern("dd/MM/yyy - HH:mm:ss");
        List<LogTicket> logs = logTicketRepo.findAllByTicketIdOrTicketNoOrderByCreatedAtAsc(tc.getId(), tc.getNo());
        for (int i = 0; i < logs.size(); i++) {
            int index = i + 1;
            LogTicket lt = logs.get(i);

            TcStatus prev = lt.getPrev();
            TcStatus curr = lt.getCurr();

            content.append(index).append(". *")
                    .append(lt.getCreatedAt()
                            .atZone(ZONE_LOCAL)
                            .toLocalDateTime()
                            .format(formatDateLog))
                    .append("*\n\t")
                    .append(lt.getMessage())
                    .append("\n\t*")
                    .append(prev == null ? "" : prev.name())
                    .append("* => *")
                    .append(curr == null ? "" : curr.name())
                    .append("*\n\n");
        }

        content.append("\n")
                .append("Pernah Diproses oleh:\n");

        UserCriteria userCriteria = UserCriteria.builder()
                .id(new StringFilter().setIn(agentQueryService.findWorkspacesByTicket(tc.getId()).stream()
                        .map(AgentWorkspace::getAgent)
                        .map(Agent::getUserId)
                        .collect(Collectors.toList())))
                .build();

        List<String> names = userQueryService.findAll(userCriteria).stream()
                .map(User::getName)
                .distinct()
                .collect(Collectors.toList());

        for (int i = 0; i < names.size(); i++) {
            int index = i + 1;
            String name = names.get(i);
            content.append(index).append(". *")
                    .append(name).append("*\n");
        }

        return SendMessage.builder()
                .chatId(TelegramContextHolder.getChatId())
                .parseMode(ParseMode.MARKDOWNV2)
                .text(TelegramUtil.esc(content.toString()))
                .build();
    }

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

        storageService.addTelegramAssets(ticket, photos);

        logTicketService.add(LogTicket.builder()
                .ticket(ticket)
                .curr(ticket.getStatus())
                .message("created")
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

        closeFlowService.confirmClose(confirmData.getValue(), !closeTicket, TicketStatusFormDTO.builder()
                .note(note)
                .build());

        ticketConfirmService.deleteById(messageId);
    }

    @Override
    public void confirmedPending(long messageId, boolean pendingTicket) {
        TicketConfirm confirmData = ticketConfirmService.findById(messageId);
        pendingFlowService.confirmPending(confirmData.getValue(), pendingTicket, new TicketStatusFormDTO());
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
        pendingFlowService.confirmPostPending(confirmData.getValue(), form);
        ticketConfirmService.deleteById(messageId);
    }

    @Override
    public void endPendingEarly(String ticketNo) {
        pendingFlowService.askPostPending(ticketNo);
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

        User user = SecurityUtil.getCurrentUser();
        if (user.hasAnyRole(AppConstants.Authority.AGENT_ROLE)) {
            boolean allowedCreate = appConfigService.getAllowAgentCreateTicket_bool()
                    .getAsBoolean();

            if (!allowedCreate)
                throw new BadRequestException("Agen tidak diperbolehkan membuat tiket sendiri");
        }

        String name = user.getName();
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

        synchronized (ISSUES_BUTTON_LIST) {
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
                    .ttl(5)
                    .build());
        }
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
                .ttl(10)
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
                .ttl(10)
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

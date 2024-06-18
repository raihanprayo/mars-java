package dev.scaraz.mars.core.service.order;

import dev.scaraz.mars.common.domain.general.TicketBotForm;
import dev.scaraz.mars.common.domain.request.TicketStatusFormDTO;
import dev.scaraz.mars.common.exception.telegram.TgError;
import dev.scaraz.mars.common.exception.telegram.TgInvalidFormError;
import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.exception.web.NotFoundException;
import dev.scaraz.mars.common.tools.annotation.FormDescriptor;
import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.common.utils.CacheConstant;
import dev.scaraz.mars.common.utils.ConfigConstants;
import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.domain.order.*;
import dev.scaraz.mars.core.domain.symptom.Issue;
import dev.scaraz.mars.core.domain.view.TicketSummary;
import dev.scaraz.mars.core.query.*;
import dev.scaraz.mars.core.query.criteria.UserCriteria;
import dev.scaraz.mars.core.repository.db.order.LogTicketRepo;
import dev.scaraz.mars.core.service.ConfigService;
import dev.scaraz.mars.core.service.StorageService;
import dev.scaraz.mars.core.service.order.flow.CloseFlowService;
import dev.scaraz.mars.core.service.order.flow.DispatchFlowService;
import dev.scaraz.mars.core.service.order.flow.PendingFlowService;
import dev.scaraz.mars.telegram.config.TelegramContextHolder;
import dev.scaraz.mars.telegram.service.TelegramBotService;
import dev.scaraz.mars.telegram.util.TelegramUtil;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.lang.reflect.Field;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static dev.scaraz.mars.common.utils.AppConstants.Telegram.REPORT_ISSUE;
import static dev.scaraz.mars.common.utils.AppConstants.ZONE_LOCAL;

@Slf4j
@RequiredArgsConstructor
@Service
public class TicketBotService {

    //    private final AppConfigService appConfigService;
    private final ApplicationContext applicationContext;
    private final ConfigService configService;
    private final TelegramBotService botService;

    private final AccountQueryService accountQueryService;
    //    private final AgentQueryService agentQueryService;
    private final AgentWorkspaceQueryService agentWorkspaceQueryService;

    private final TicketService service;
    private final TicketFlowService flowService;
    private final TicketQueryService queryService;
    private final TicketSummaryQueryService summaryQueryService;
    private final LogTicketRepo logTicketRepo;

    private final IssueQueryService issueQueryService;


    private final TicketFormService ticketFormService;
    private final CloseFlowService closeFlowService;
    private final PendingFlowService pendingFlowService;
    private final DispatchFlowService dispatchFlowService;

    private final ConfirmService confirmService;

    private final StorageService storageService;
    private final LogTicketService logTicketService;

    @Transactional(readOnly = true)
    public SendMessage info(String ticketNo) {
        TicketSummary tc = summaryQueryService.findByIdOrNo(ticketNo);
        Map<String, Object> infoMap = new LinkedHashMap<>();
        infoMap.put("No", tc.getNo());
        infoMap.put("Witel", tc.getWitel());
        infoMap.put("STO", tc.getSto());
        infoMap.put("Requestor", tc.getSenderName());
        infoMap.put("Gaul", tc.isGaul() ? "Ya" : "Tidak");
        infoMap.put("No Service", tc.getServiceNo());
        infoMap.put("Tiket NOSSA", tc.getIncidentNo());
        infoMap.put("Status", tc.getStatus());

        StringBuilder content = new StringBuilder()
                .append("Informasi Tiket:\n");

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

        UserCriteria userCriteria = new UserCriteria()
                .setId(new StringFilter().setIn(agentWorkspaceQueryService.findWorkspacesByTicket(tc.getId()).stream()
                        .map(AgentWorkspace::getAccount)
                        .map(Account::getId)
                        .collect(Collectors.toList())));

        List<String> names = accountQueryService.findAll(userCriteria).stream()
                .map(Account::getName)
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

    @Transactional
    public Ticket registerForm(TicketBotForm form,
                               @Nullable Collection<PhotoSize> photos
    ) {
        return registerForm(form, photos, null);
    }

    @Transactional
    public Ticket registerForm(TicketBotForm form,
                               @Nullable Collection<PhotoSize> photos,
                               @Nullable Document document
    ) {
        Issue issue = issueQueryService.findById((Long) form.getIssueId())
                .orElseThrow(() -> NotFoundException.entity(Issue.class, "id", form.getIssueId()));

        int totalGaul = queryService.countGaul((Long) form.getIssueId(), form.getService());

        Account account = accountQueryService.findByCurrentAccess();
        Ticket ticket = service.save(Ticket.builder()
                .witel(form.getWitel() == null ? account.getWitel() : form.getWitel())
                .sto(form.getSto() == null ? account.getSto() : form.getSto())
                .issue(TcIssue.from(issue))
                .incidentNo(form.getIncident())
                .serviceNo(form.getService())
                .source(form.getSource())
                .senderId(account.getTg().getId())
                .senderName(account.getName())
                .note(form.getNote())
                .gaul(totalGaul)
                .build());

        log.info("REGISTERED NEW TICKET -- TICKET NO={} GAUL={} TYPE={}/{}",
                ticket.getNo(),
                totalGaul != 0,
                issue.getProduct(), issue.getName());

        storageService.addTelegramAssets(ticket, photos);
        storageService.addTelegramAssets(ticket, document);

        logTicketService.add(LogTicket.builder()
                .ticket(ticket)
                .curr(ticket.getStatus())
                .message("created")
                .build());

        return ticket;
    }

    public Ticket take(String ticketNo) {
        return flowService.take(ticketNo);
    }

    @Transactional
    public void confirmedClose(
            long messageId,
            boolean closeTicket,
            @Nullable String note,
            List<PhotoSize> photos) {
        TicketConfirm confirmData = confirmService.findById(messageId);

        closeFlowService.confirmClose(confirmData.getValue(), !closeTicket, TicketStatusFormDTO.builder()
                .note(note)
                .photos(photos)
                .build());

        confirmService.deleteById(messageId);
    }

    public void confirmedPending(long messageId, boolean pendingTicket) {
        TicketConfirm confirmData = confirmService.findById(messageId);
        pendingFlowService.confirmPending(confirmData.getValue(), pendingTicket, new TicketStatusFormDTO());
        confirmService.deleteById(messageId);
    }

    public void confirmedPostPending(long messageId, @Nullable String text, @Nullable Collection<PhotoSize> photos) {
        TicketConfirm confirmData = confirmService.findById(messageId);
        TicketStatusFormDTO form = TicketStatusFormDTO.builder()
                .status(text == null ? TcStatus.CLOSED : TcStatus.REOPEN)
                .note(text)
                .build();

        if (photos != null && !photos.isEmpty())
            form.setPhotos(new ArrayList<>(photos));

        pendingFlowService.confirmPostPending(confirmData.getValue(), form);
        confirmService.deleteById(messageId);
    }

    public void confirmedPostPendingConfirmation(long messageId, boolean agree, @Nullable String text, @Nullable Collection<PhotoSize> photos) {
        TicketConfirm confirmData = confirmService.findById(messageId);
        TicketStatusFormDTO form = TicketStatusFormDTO.builder()
                .status(text == null ? TcStatus.CLOSED : TcStatus.REOPEN)
                .note(text)
                .build();

        if (photos != null && !photos.isEmpty())
            form.setPhotos(new ArrayList<>(photos));

        pendingFlowService.confirmPostPendingConfirmation(confirmData.getValue(), form);
        confirmService.deleteById(messageId);
    }

    @Transactional
    public void endPendingEarly(long messageId, String ticketNo) {
        pendingFlowService.askPostPending(ticketNo);
        confirmService.deleteById(messageId);
    }

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
        ticketFormService.checkFieldSto(form);
    }

    @Cacheable(
            cacheNames = CacheConstant.ISSUES_KEYBOARD,
            key = "'telegram'",
            unless = "#result == null")
    public InlineKeyboardMarkup createIssueKeyboarButtons() {
        List<Issue> issues = issueQueryService.findAllNotDeleted();
        if (issues.isEmpty())
            throw new BadRequestException("Pilihan kendala kosong, Silahkan menghubungi admin MARS, untuk meminta menambahkan kendala");

        MultiValueMap<Product, InlineKeyboardButton> buttonGroups = new LinkedMultiValueMap<>();

        int colCount = configService.get(ConfigConstants.TG_START_CMD_ISSUE_COLUMN_INT).getAsInt();

        for (Issue issue : issues) {
            Product product = issue.getProduct();

            String name = StringUtils.isNotBlank(issue.getAlias()) ?
                    issue.getAlias() : issue.getName();

            buttonGroups.add(product, InlineKeyboardButton.builder()
                    .text(name)
                    .callbackData(REPORT_ISSUE + issue.getId())
                    .build());
        }

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        for (Product product : Product.values()) {
            buttons.add(List.of(InlineKeyboardButton.builder()
                    .text(product.name())
                    .callbackData("DUMMY")
                    .build()));

            List<InlineKeyboardButton> row = new ArrayList<>();
            buttons.add(row);

            List<InlineKeyboardButton> defined = buttonGroups.getOrDefault(product, new ArrayList<>());
            for (int i = 0; i < defined.size(); i++) {
                if (i != 0 && (i % colCount) == 0) {
                    row = new ArrayList<>();
                    buttons.add(row);
                }

                InlineKeyboardButton e = defined.get(i);
                log.trace("Issue Inline Button: {}", e);
                row.add(e);
            }

            if (row.size() < colCount) {
                int total = colCount - row.size();
                for (int i = total; i > 0; i--) {
                    row.add(InlineKeyboardButton.builder()
                            .text("-")
                            .callbackData("DUMMY")
                            .build());
                }
            }
        }


        return new InlineKeyboardMarkup(buttons);
    }

}

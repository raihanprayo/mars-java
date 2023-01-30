package dev.scaraz.mars.core.service.order.impl;

import dev.scaraz.mars.common.domain.request.TicketStatusFormDTO;
import dev.scaraz.mars.common.tools.annotation.FormDescriptor;
import dev.scaraz.mars.common.domain.general.TicketBotForm;
import dev.scaraz.mars.common.exception.telegram.TgError;
import dev.scaraz.mars.common.exception.telegram.TgInvalidFormError;
import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.common.tools.filter.type.ProductFilter;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.core.domain.order.TicketConfirm;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.domain.order.Issue;
import dev.scaraz.mars.core.domain.order.LogTicket;
import dev.scaraz.mars.core.domain.order.Ticket;
import dev.scaraz.mars.core.query.IssueQueryService;
import dev.scaraz.mars.core.query.TicketQueryService;
import dev.scaraz.mars.core.query.criteria.IssueCriteria;
import dev.scaraz.mars.core.service.StorageService;
import dev.scaraz.mars.core.service.order.*;
import dev.scaraz.mars.core.util.SecurityUtil;
import dev.scaraz.mars.core.util.Util;
import dev.scaraz.mars.telegram.service.TelegramBotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class TicketBotServiceImpl implements TicketBotService {

    private final TelegramBotService botService;

    private final TicketService service;
    private final TicketFlowService flowService;
    private final TicketQueryService queryService;
    private final IssueQueryService issueQueryService;

    private final StorageService storageService;
    private final TicketConfirmService ticketConfirmService;
    private final LogTicketService logTicketService;

    @Override
    @Transactional
    public Ticket registerForm(TicketBotForm form, Collection<PhotoSize> photos) {
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
            if (fieldName.equals("description")) continue;
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
        checkFieldIncidentNo(form);
        checkFieldServiceNo(form);
        checkFieldIssue(form);
    }

    private void checkFieldIncidentNo(TicketBotForm form) {
        final String FIELD_NAME = "incident";
        FormDescriptor formDescriptor = TicketBotForm.getDescriptors().get(FIELD_NAME);
        String incident = form.getIncident();
        if (!incident.toUpperCase().startsWith("IN")) {
            throw new TgInvalidFormError(
                    FIELD_NAME,
                    "error.ticket.form.incident",
                    List.of(formDescriptor.alias())
            );
        }

        String substring = incident.substring(2);
        if (!Util.isStringNumber(substring)) {
            throw new TgInvalidFormError(
                    FIELD_NAME,
                    "error.ticket.form.incident",
                    List.of(formDescriptor.alias())
            );
        }
    }

    private void checkFieldServiceNo(TicketBotForm form) {
        final String FIELD_NAME = "service";
        FormDescriptor formDescriptor = TicketBotForm.getDescriptors().get(FIELD_NAME);
        String serviceNo = form.getService();

        boolean isIptvInternet = List.of(Product.IPTV, Product.INTERNET).contains(form.getProduct());
        if (isIptvInternet) {
            boolean strNumbrAndPrefix1 = Util.isStringNumber(serviceNo) &&
                    serviceNo.startsWith("1");

            if (!strNumbrAndPrefix1) {
                throw new TgInvalidFormError(
                        FIELD_NAME,
                        "error.ticket.form.service",
                        List.of(formDescriptor.alias())
                );
            }
        }
        else {
            boolean prefixPlus = serviceNo.startsWith("+");
            boolean strNumbr = Util.isStringNumber(prefixPlus ?
                    serviceNo.substring(1) :
                    serviceNo
            );

            if (!strNumbr) {
                throw new TgInvalidFormError(
                        FIELD_NAME,
                        "error.ticket.form.service",
                        List.of(formDescriptor.alias())
                );
            }
        }
    }

    private void checkFieldIssue(TicketBotForm form) {
        final String FIELD_NAME = "issue";
        FormDescriptor formDescriptor = TicketBotForm.getDescriptors().get(FIELD_NAME);

        String problem = form.getIssue();
        IssueCriteria criteria = IssueCriteria.builder()
                .name(new StringFilter().setLike(problem))
                .product(new ProductFilter().setEq(form.getProduct()))
                .build();

        Optional<Issue> issueOpt = issueQueryService.findOne(criteria);

        if (issueOpt.isPresent()) {
            form.setIssueId(issueOpt.get().getId());
        }
        else {
            throw new TgInvalidFormError(
                    FIELD_NAME,
                    "error.ticket.form.problem",
                    List.of(formDescriptor.alias())
            );
        }
    }

}

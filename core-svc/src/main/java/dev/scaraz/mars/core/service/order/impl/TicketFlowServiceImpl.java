package dev.scaraz.mars.core.service.order.impl;

import dev.scaraz.mars.common.domain.request.TicketStatusFormDTO;
import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.exception.web.InternalServerException;
import dev.scaraz.mars.common.tools.Translator;
import dev.scaraz.mars.common.tools.enums.AgStatus;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.core.domain.order.TicketConfirm;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.domain.order.LogTicket;
import dev.scaraz.mars.core.domain.order.Ticket;
import dev.scaraz.mars.core.domain.order.TicketAgent;
import dev.scaraz.mars.core.domain.view.TicketSummary;
import dev.scaraz.mars.core.query.TicketAgentQueryService;
import dev.scaraz.mars.core.query.TicketQueryService;
import dev.scaraz.mars.core.query.TicketSummaryQueryService;
import dev.scaraz.mars.core.query.criteria.TicketAgentCriteria;
import dev.scaraz.mars.core.repository.order.TicketAgentRepo;
import dev.scaraz.mars.core.service.AppConfigService;
import dev.scaraz.mars.core.service.NotifierService;
import dev.scaraz.mars.core.service.StorageService;
import dev.scaraz.mars.core.service.order.LogTicketService;
import dev.scaraz.mars.core.service.order.TicketConfirmService;
import dev.scaraz.mars.core.service.order.TicketFlowService;
import dev.scaraz.mars.core.service.order.TicketService;
import dev.scaraz.mars.core.util.SecurityUtil;
import dev.scaraz.mars.telegram.config.TelegramContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor

@Service
public class TicketFlowServiceImpl implements TicketFlowService {

    private final AppConfigService appConfigService;
    private final TicketService service;
    private final TicketQueryService queryService;
    private final TicketSummaryQueryService summaryQueryService;

    private final TicketAgentRepo agentRepo;
    private final TicketAgentQueryService agentQueryService;

    private final TicketConfirmService ticketConfirmService;

    private final NotifierService notifierService;
    private final StorageService storageService;

    private final LogTicketService logTicketService;

    @Override
    @Transactional
    public Ticket take(String ticketIdOrNo) {
        // Pengambilan tiket dengan kondisi tiket belum dikerjakan
        Ticket ticket = queryService.findByIdOrNo(ticketIdOrNo);

        if (summaryQueryService.isWorkInProgressByTicketId(ticket.getId())) {
            throw BadRequestException.args("error.ticket.taken");
        }
        else if (!List.of(TcStatus.OPEN, TcStatus.DISPATCH).contains(ticket.getStatus())) {
            throw BadRequestException.args("Invalid status pengambilan tiket");
        }

        User user = SecurityUtil.getCurrentUser();
        if (user != null) {
            TcStatus prevStatus = ticket.getStatus();
            TicketAgent agent = agentRepo.save(TicketAgent.builder()
                    .ticket(ticket)
                    .user(user)
                    .status(AgStatus.PROGRESS)
                    .build());

            ticket.setStatus(TcStatus.PROGRESS);

            boolean isPreviousStatusOpen = prevStatus == TcStatus.OPEN;
            if (isPreviousStatusOpen) notifierService.sendTaken(ticket, user);
            else notifierService.sendRetaken(ticket, user);

            logTicketService.add(LogTicket.builder()
                    .ticket(ticket)
                    .prev(prevStatus)
                    .curr(ticket.getStatus())
                    .agent(agent)
                    .message(isPreviousStatusOpen ? LOG_WORK_IN_PROGRESS : LOG_REWORK_IN_PROGRESS)
                    .build());

            return service.save(ticket);
        }

        throw InternalServerException.args("Unable to decide which user as agent");
    }

    @Override
    @Transactional
    public Ticket close(String ticketIdOrNo, TicketStatusFormDTO form) {
        log.info("CLOSE FORM {}", form);

        Ticket ticket = queryService.findByIdOrNo(ticketIdOrNo);
        TicketSummary summary = summaryQueryService.findByIdOrNo(ticketIdOrNo);

        if (!summary.isWip())
            throw BadRequestException.args("error.ticket.update.stat");
        else if (!summary.getWipBy().getId().equals(SecurityUtil.getCurrentUser().getId()))
            throw BadRequestException.args("error.ticket.update.stat.agent");

        TicketAgent agent = agentRepo.updateStatusAndCloseStatusAndCloseDesc(
                summary.getWipId(),
                AgStatus.CLOSED,
                TcStatus.CLOSED,
                form.getSolution(),
                form.getNote());

        int minute = appConfigService.getCloseConfirm_int()
                .getAsNumber()
                .intValue();

        int messageId = notifierService.sendCloseConfirmation(ticket, minute, form);
        ticket.setStatus(TcStatus.CONFIRMATION);
        ticket.setConfirmMessageId((long) messageId);

        ticketConfirmService.save(TicketConfirm.builder()
                .id(messageId)
                .no(ticket.getNo())
                .status(TicketConfirm.CLOSED)
                .ttl(minute)
                .build());

        log.info("NOTIF SENDED TO USER -- MESSAGE ID {}", messageId);
        logTicketService.add(LogTicket.builder()
                .ticket(ticket)
                .prev(TcStatus.PROGRESS)
                .curr(ticket.getStatus())
                .agent(agent)
                .message(LOG_CLOSE_CONFIRMATION)
                .build());

        if (form.getFiles() != null)
            storageService.addPhotoForAgentAsync(ticket, agent, List.of(form.getFiles()));

        return service.save(ticket);
    }

    @Override
    @Transactional
    public Ticket pending(String ticketIdOrNo, TicketStatusFormDTO form) {
        log.info("PENDING FORM {}", form);
        Ticket ticket = queryService.findByIdOrNo(ticketIdOrNo);
        TicketSummary summary = summaryQueryService.findByIdOrNo(ticketIdOrNo);

        if (!summary.isWip())
            throw BadRequestException.args("error.ticket.update.stat");
        else if (!summary.getWipBy().getId().equals(SecurityUtil.getCurrentUser().getId()))
            throw BadRequestException.args("error.ticket.update.stat.agent");

        final TcStatus prevStatus = ticket.getStatus();
        TicketAgent agent = agentRepo.updateStatusAndCloseStatusAndCloseDesc(
                summary.getWipId(),
                AgStatus.CLOSED,
                TcStatus.PENDING,
                form.getSolution(),
                form.getNote());

        if (form.getNote() == null || StringUtils.isBlank(form.getNote().trim()))
            throw BadRequestException.args("Pending worklog cannot be empty");


        int minute = appConfigService.getCloseConfirm_int()
                .getAsNumber()
                .intValue();

        int messageId = notifierService.sendPendingConfirmation(ticket, minute, form);
        ticket.setStatus(TcStatus.CONFIRMATION);
        ticket.setConfirmMessageId((long) messageId);

        ticketConfirmService.save(TicketConfirm.builder()
                .id(messageId)
                .no(ticket.getNo())
                .status(TicketConfirm.PENDING)
                .ttl(minute)
                .build());

        log.info("NOTIF PENDING SENDED TO USER -- MESSAGE ID {}", messageId);
        logTicketService.add(LogTicket.builder()
                .ticket(ticket)
                .prev(prevStatus)
                .curr(ticket.getStatus())
                .agent(agent)
                .message(LOG_PENDING_CONFIRMATION)
                .build());

        return service.save(ticket);
    }

    @Override
    @Transactional
    public Ticket dispatch(String ticketIdOrNo, TicketStatusFormDTO form) {
        Ticket ticket = queryService.findByIdOrNo(ticketIdOrNo);
        TicketSummary summary = summaryQueryService.findByIdOrNo(ticketIdOrNo);

        if (!summary.isWip())
            throw BadRequestException.args("error.ticket.update.stat");
        else if (!summary.getWipBy().getId().equals(SecurityUtil.getCurrentUser().getId()))
            throw BadRequestException.args("error.ticket.update.stat.agent");

        TicketAgent agent = agentRepo.updateStatusAndCloseStatusAndCloseDesc(
                summary.getWipId(),
                AgStatus.CLOSED,
                TcStatus.DISPATCH,
                null,
                form.getNote());

        if (form.getFiles() != null)
            storageService.addPhotoForAgentAsync(ticket, agent, List.of(form.getFiles()));

        ticket.setStatus(TcStatus.DISPATCH);

        notifierService.send(ticket.getSenderId(),
                "tg.ticket.update.dispatch",
                ticket.getNo());

        logTicketService.add(LogTicket.builder()
                .ticket(ticket)
                .prev(TcStatus.PROGRESS)
                .curr(ticket.getStatus())
                .agent(agent)
                .message(LOG_DISPATCH_REQUEST)
                .build());
        return service.save(ticket);
    }


    @Override
    @Transactional
    public Ticket confirmClose(String ticketIdOrNo, boolean reopen, TicketStatusFormDTO form) {
        log.info("TICKET CLOSE CONFIRM REQUEST OF NO {} -- REOPEN ? {}", ticketIdOrNo, reopen);
        Ticket ticket = queryService.findByIdOrNo(ticketIdOrNo);

        if (ticket.getStatus() != TcStatus.CONFIRMATION)
            throw BadRequestException.args("error.ticket.illegal.confirm.state");

        boolean isRequestor = SecurityUtil.getCurrentUser()
                .getTg().getId() == ticket.getSenderId();
        if (!isRequestor)
            throw BadRequestException.args("Invalid requestor owner");

        TicketAgent prevAgent = agentQueryService.findAll(
                TicketAgentCriteria.builder()
                        .ticketId(new StringFilter().setEq(ticket.getId()))
                        .build(),
                PageRequest.of(0, 1, Sort.Direction.DESC, "createdAt")
        ).stream().findFirst().orElseThrow();

        if (reopen) {
            ticket.setStatus(TcStatus.REOPEN);
            ticket.setConfirmMessageId(null);

            String reopenDesc = StringUtils.isNoneBlank(form.getNote()) ?
                    form.getNote() : "<no description>";

            agentRepo.save(TicketAgent.builder()
                    .ticket(ticket)
                    .status(AgStatus.PROGRESS)
                    .user(prevAgent.getUser())
                    .reopenDescription(reopenDesc)
                    .build());

            logTicketService.add(LogTicket.builder()
                    .ticket(ticket)
                    .prev(TcStatus.CONFIRMATION)
                    .curr(ticket.getStatus())
                    .message(LOG_REOPEN)
                    .build());

            notifierService.safeSend(
                    prevAgent.getUser().getTg().getId(),
                    "tg.ticket.confirm.reopen.agent",
                    ticket.getNo(),
                    reopenDesc);
        }
        else {
            ticket.setStatus(TcStatus.CLOSED);
            ticket.setConfirmMessageId(null);

            String logMessage;
            if (TelegramContextHolder.hasContext()) {
                logMessage = LOG_CONFIRMED_CLOSE;
                String message = Translator.tr(
                        "tg.ticket.confirm.closed",
                        ticket.getNo(),
                        Translator.tr("app.done.watermark")
                );

                notifierService.send(ticket.getSenderId(), message);

                log.info("NOTIFY AGENT -- ID {}", prevAgent.getUser().getTg().getId());
                notifierService.safeSend(prevAgent.getUser().getTg().getId(),
                        "tg.ticket.confirm.closed.agent",
                        ticket.getNo(),
                        Translator.tr("app.done.watermark",
                                notifierService.useLocale(prevAgent.getUser().getTg().getId())));
            }
            else {
                logMessage = LOG_AUTO_CLOSE;
                notifierService.send(ticket.getSenderId(),
                        "tg.ticket.confirm.auto-closed",
                        ticket.getNo());
            }

            logTicketService.add(LogTicket.builder()
                    .ticket(ticket)
                    .prev(TcStatus.CONFIRMATION)
                    .curr(ticket.getStatus())
                    .message(logMessage)
                    .build());
        }


        return service.save(ticket);
    }

    @Async
    @Override
    public void confirmCloseAsync(String ticketIdOrNo, boolean reopen, TicketStatusFormDTO form) {
        confirmClose(ticketIdOrNo, reopen, form);
    }


    @Override
    @Transactional
    public Ticket confirmPending(String ticketIdOrNo, boolean doPending, TicketStatusFormDTO form) {
        log.info("TICKET CLOSE CONFIRM REQUEST OF NO {} -- PENDING ? {}", ticketIdOrNo, doPending);
        Ticket ticket = queryService.findByIdOrNo(ticketIdOrNo);

        boolean isRequestor = SecurityUtil.getCurrentUser()
                .getTg().getId() == ticket.getSenderId();
        if (ticket.getStatus() != TcStatus.CONFIRMATION)
            throw BadRequestException.args("error.ticket.illegal.confirm.state");
        if (!isRequestor)
            throw BadRequestException.args("Invalid requestor owner");

        if (doPending) {
            ticket.setStatus(TcStatus.PENDING);
            ticket.setConfirmMessageId(null);

            int minute = appConfigService.getPostPending_int()
                    .getAsNumber()
                    .intValue();

            int messageId = notifierService.sendRaw(ticket.getSenderId(),
                    String.format("Tiket *%s* - *PENDING*", ticket.getNo()),
                    "",
                    "_MARS akan kembali dalam " + minute + " menit_"
            );

            ticketConfirmService.save(TicketConfirm.builder()
                    .id(messageId)
                    .no(ticket.getNo())
                    .status(TicketConfirm.POST_PENDING)
                    .ttl(minute)
                    .build());

            ticket.setConfirmPendingMessageId((long) messageId);

            logTicketService.add(LogTicket.builder()
                    .ticket(ticket)
                    .prev(TcStatus.CONFIRMATION)
                    .curr(ticket.getStatus())
                    .message(LOG_CONFIRMED_PENDING)
                    .build());
        }
        else {
            ticket.setStatus(TcStatus.CLOSED);
            ticket.setConfirmMessageId(null);

            String logMessage;
            if (TelegramContextHolder.hasContext()) {
                logMessage = LOG_CONFIRMED_CLOSE;
            }
            else {
                logMessage = LOG_AUTO_CLOSE;
                notifierService.send(ticket.getSenderId(),
                        "tg.ticket.confirm.auto-closed",
                        ticket.getNo());
            }

            logTicketService.add(LogTicket.builder()
                    .ticket(ticket)
                    .prev(TcStatus.CONFIRMATION)
                    .curr(ticket.getStatus())
                    .message(logMessage)
                    .build());
        }

        return service.save(ticket);
    }

    @Override
    public void confirmPendingAsync(String ticketIdOrNo, boolean doPending, TicketStatusFormDTO form) {
        confirmPending(ticketIdOrNo, doPending, form);
    }

    @Override
    @Transactional
    public void askPostPending(String ticketNo) {
        Ticket ticket = queryService.findByIdOrNo(ticketNo);
        ticket.setConfirmPendingMessageId(null);

        int minute = appConfigService.getCloseConfirm_int()
                .getAsNumber()
                .intValue();

        int messageId = notifierService.sendPostPendingConfirmation(ticket, minute);
        ticketConfirmService.save(TicketConfirm.builder()
                .id(messageId)
                .no(ticketNo)
                .ttl(minute)
                .status(TicketConfirm.POST_PENDING_CONFIRMATION)
                .build());

        ticket.setConfirmMessageId((long) messageId);
        service.save(ticket);
    }

    @Override
    @Transactional
    public Ticket confirmPostPending(String ticketNo, TicketStatusFormDTO form) {
        Ticket ticket = queryService.findByIdOrNo(ticketNo);
        TcStatus prevStatus = ticket.getStatus();

        TicketAgent prevAgent = agentQueryService.findAll(
                TicketAgentCriteria.builder()
                        .ticketId(new StringFilter().setEq(ticket.getId()))
                        .build(),
                PageRequest.of(0, 1, Sort.Direction.DESC, "createdAt")
        ).stream().findFirst().orElseThrow();

        // User menjawab belum
        if (form.getStatus() == TcStatus.REOPEN) {
            ticket.setStatus(TcStatus.REOPEN);
            ticket.setConfirmMessageId(null);

            String reopenDesc = StringUtils.isNoneBlank(form.getNote()) ?
                    form.getNote() :
                    "<no description>";

            agentRepo.save(TicketAgent.builder()
                    .ticket(ticket)
                    .status(AgStatus.PROGRESS)
                    .user(prevAgent.getUser())
                    .reopenDescription(reopenDesc)
                    .build());

            logTicketService.add(LogTicket.builder()
                    .ticket(ticket)
                    .prev(prevStatus)
                    .curr(ticket.getStatus())
                    .message(LOG_REOPEN)
                    .build());

            notifierService.safeSend(
                    prevAgent.getUser().getTg().getId(),
                    "tg.ticket.confirm.reopen.agent",
                    ticket.getNo(),
                    reopenDesc);

            if (form.getPhotos() != null) {
                storageService.addPhotoForTicketAsync(
                        form.getPhotos(),
                        ticket
                );
            }
        }
        // User menjawab sudah
        else {
            ticket.setStatus(TcStatus.CLOSED);
            ticket.setConfirmMessageId(null);

            String logMessage;
            if (TelegramContextHolder.hasContext()) {
                logMessage = LOG_CONFIRMED_CLOSE;
                notifierService.sendRaw(ticket.getSenderId(),
                        "Tiket: *" + ticketNo + "*: telah selesai dikerjakan",
                        "",
                        "Eksekutor: *" + prevAgent.getUser().getName() + "*",
                        "Status: *" + ticket.getStatus() + "*",
                        "",
                        "_Terima Kasih telah menggunakan *MARS*_"
                );

                log.info("NOTIFY AGENT -- ID {}", prevAgent.getUser().getTg().getId());
                notifierService.safeSend(prevAgent.getUser().getTg().getId(),
                        "tg.ticket.confirm.closed.agent",
                        ticket.getNo(),
                        Translator.tr("app.done.watermark",
                                notifierService.useLocale(prevAgent.getUser().getTg().getId())));
            }
            else {
                logMessage = LOG_AUTO_CLOSE;
                notifierService.send(ticket.getSenderId(),
                        "tg.ticket.confirm.auto-closed",
                        ticket.getNo());
            }

            logTicketService.add(LogTicket.builder()
                    .ticket(ticket)
                    .prev(prevStatus)
                    .curr(ticket.getStatus())
                    .message(logMessage)
                    .build());
        }

        return service.save(ticket);
    }

}

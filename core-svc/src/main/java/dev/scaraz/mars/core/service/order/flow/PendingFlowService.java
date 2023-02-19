package dev.scaraz.mars.core.service.order.flow;

import dev.scaraz.mars.common.domain.request.TicketStatusFormDTO;
import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.tools.Translator;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.domain.order.*;
import dev.scaraz.mars.core.query.AgentQueryService;
import dev.scaraz.mars.core.query.TicketQueryService;
import dev.scaraz.mars.core.query.UserQueryService;
import dev.scaraz.mars.core.service.AppConfigService;
import dev.scaraz.mars.core.service.NotifierService;
import dev.scaraz.mars.core.service.StorageService;
import dev.scaraz.mars.core.service.order.AgentService;
import dev.scaraz.mars.core.service.order.LogTicketService;
import dev.scaraz.mars.core.service.order.TicketConfirmService;
import dev.scaraz.mars.core.service.order.TicketService;
import dev.scaraz.mars.core.util.SecurityUtil;
import dev.scaraz.mars.telegram.config.TelegramContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor

@Service
public class PendingFlowService {

    private final AppConfigService appConfigService;

    private final TicketService service;
    private final TicketQueryService queryService;
    //    private final TicketSummaryQueryService summaryQueryService;
    private final TicketConfirmService ticketConfirmService;
    private final LogTicketService logTicketService;

    private final AgentService agentService;
    private final AgentQueryService agentQueryService;

    private final UserQueryService userQueryService;

    private final NotifierService notifierService;
    private final StorageService storageService;


    @Transactional
    public Ticket pending(String ticketIdOrNo, TicketStatusFormDTO form) {
        log.info("PENDING FORM {}", form);
        Ticket ticket = queryService.findByIdOrNo(ticketIdOrNo);
//        TicketSummary summary = summaryQueryService.findByIdOrNo(ticketIdOrNo);

//        if (!summary.isWip())
//            throw BadRequestException.args("error.ticket.update.stat");
//        else if (!summary.getWipBy().getId().equals(SecurityUtil.getCurrentUser().getId()))
//            throw BadRequestException.args("error.ticket.update.stat.agent");

        final TcStatus prevStatus = ticket.getStatus();
        AgentWorkspace workspace = agentQueryService.getLastWorkspace(ticket.getId());
        Agent agent = workspace.getAgent();

        workspace.getLastWorklog().ifPresent(worklog -> {
            worklog.setCloseStatus(TcStatus.PENDING);
            worklog.setSolution(form.getSolution());
            worklog.setMessage(form.getNote());
            agentService.save(worklog);
        });

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
                .value(ticket.getNo())
                .status(TicketConfirm.PENDING)
                .ttl(minute)
                .build());

        log.info("NOTIF PENDING SENDED TO USER -- MESSAGE ID {}", messageId);
        logTicketService.add(LogTicket.builder()
                .ticket(ticket)
                .prev(prevStatus)
                .curr(ticket.getStatus())
                .agentId(agent.getId())
                .message(LogTicketService.LOG_PENDING_CONFIRMATION)
                .build());

        return service.save(ticket);
    }

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

        AgentWorkspace workspace = agentQueryService.getLastWorkspace(ticket.getId());

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
                    .value(ticket.getNo())
                    .status(TicketConfirm.POST_PENDING)
                    .ttl(minute)
                    .build());

            ticket.setConfirmPendingMessageId((long) messageId);

            logTicketService.add(LogTicket.builder()
                    .ticket(ticket)
                    .prev(TcStatus.CONFIRMATION)
                    .curr(ticket.getStatus())
                    .message(LogTicketService.LOG_CONFIRMED_PENDING)
                    .build());
        }
        else {
            ticket.setStatus(TcStatus.CLOSED);
            ticket.setConfirmMessageId(null);

            String logMessage;
            if (TelegramContextHolder.hasContext()) {
                logMessage = LogTicketService.LOG_CONFIRMED_CLOSE;
            }
            else {
                logMessage = LogTicketService.LOG_AUTO_CLOSE;
                notifierService.send(ticket.getSenderId(),
                        "tg.ticket.confirm.auto-closed",
                        ticket.getNo());
            }

            workspace.getLastWorklog().ifPresent(worklog -> {
                worklog.setCloseStatus(TcStatus.CLOSED);
                agentService.save(worklog);
            });

            logTicketService.add(LogTicket.builder()
                    .ticket(ticket)
                    .prev(TcStatus.CONFIRMATION)
                    .curr(ticket.getStatus())
                    .message(logMessage)
                    .build());
        }

        return service.save(ticket);
    }

    public void confirmPendingAsync(String ticketIdOrNo, boolean doPending, TicketStatusFormDTO form) {
        confirmPending(ticketIdOrNo, doPending, form);
    }

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
                .value(ticketNo)
                .ttl(minute)
                .status(TicketConfirm.POST_PENDING_CONFIRMATION)
                .build());

        ticket.setConfirmMessageId((long) messageId);
        service.save(ticket);
    }

    @Transactional
    public Ticket confirmPostPending(String ticketNo, TicketStatusFormDTO form) {
        Ticket ticket = queryService.findByIdOrNo(ticketNo);
        TcStatus prevStatus = ticket.getStatus();

//        Agent prevAgent = agentQueryService.findAll(
//                TicketAgentCriteria.builder()
//                        .ticketId(new StringFilter().setEq(ticket.getId()))
//                        .build(),
//                PageRequest.of(0, 1, Sort.Direction.DESC, "createdAt")
//        ).stream().findFirst().orElseThrow();

        AgentWorkspace workspace = agentQueryService.getLastWorkspace(ticket.getId());
        Agent agent = workspace.getAgent();

        // Requestor menjawab belum
        if (form.getStatus() == TcStatus.REOPEN) {
            ticket.setStatus(TcStatus.REOPEN);
            ticket.setConfirmMessageId(null);

            String reopenDesc = StringUtils.isNoneBlank(form.getNote()) ?
                    form.getNote() :
                    "<no description>";

//            agentRepo.save(Agent.builder()
//                    .ticketId(ticket.getId())
//                    .status(AgStatus.PROGRESS)
//                    .user(prevAgent.getUser())
//                    .reopenDescription(reopenDesc)
//                    .build());


            logTicketService.add(LogTicket.builder()
                    .ticket(ticket)
                    .prev(prevStatus)
                    .curr(ticket.getStatus())
                    .message(LogTicketService.LOG_REOPEN)
                    .build());

            notifierService.safeSend(
                    agent.getTelegramId(),
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
        // Requestor menjawab sudah
        else {
            ticket.setStatus(TcStatus.CLOSED);
            ticket.setConfirmMessageId(null);

            String logMessage;
            if (TelegramContextHolder.hasContext()) {
                logMessage = LogTicketService.LOG_CONFIRMED_CLOSE;

                Optional<User> userOpt = userQueryService.findByIdOpt(agent.getUserId());
                notifierService.sendRaw(ticket.getSenderId(),
                        "Tiket: *" + ticketNo + "*: telah selesai dikerjakan",
                        "",
                        "Eksekutor: *" + userOpt.map(User::getName).orElse("_<Unknown User>_") + "*",
                        "Status: *" + ticket.getStatus() + "*",
                        "",
                        "_Terima Kasih telah menggunakan *MARS*_"
                );

                if (userOpt.isPresent()) {
                    notifierService.safeSend(agent.getTelegramId(),
                            "tg.ticket.confirm.closed.agent",
                            ticket.getNo(),
                            Translator.tr("app.done.watermark"));
                }
            }
            else {
                logMessage = LogTicketService.LOG_AUTO_CLOSE;
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

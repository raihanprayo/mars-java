package dev.scaraz.mars.core.service.order.flow;

import dev.scaraz.mars.common.domain.request.TicketStatusFormDTO;
import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.tools.Translator;
import dev.scaraz.mars.common.tools.enums.AgStatus;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.core.domain.order.*;
import dev.scaraz.mars.core.domain.view.TicketSummary;
import dev.scaraz.mars.core.query.AgentQueryService;
import dev.scaraz.mars.core.query.TicketQueryService;
import dev.scaraz.mars.core.query.TicketSummaryQueryService;
import dev.scaraz.mars.core.repository.order.AgentRepo;
import dev.scaraz.mars.core.service.AppConfigService;
import dev.scaraz.mars.core.service.NotifierService;
import dev.scaraz.mars.core.service.StorageService;
import dev.scaraz.mars.core.service.order.AgentService;
import dev.scaraz.mars.core.service.order.LogTicketService;
import dev.scaraz.mars.core.service.order.ConfirmService;
import dev.scaraz.mars.core.service.order.TicketService;
import dev.scaraz.mars.core.util.SecurityUtil;
import dev.scaraz.mars.telegram.config.TelegramContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static dev.scaraz.mars.core.service.order.LogTicketService.*;

@Slf4j
@RequiredArgsConstructor

@Service
public class CloseFlowService {

    private final AppConfigService appConfigService;

    private final TicketService service;
    private final TicketQueryService queryService;
    private final TicketSummaryQueryService summaryQueryService;
    private final ConfirmService confirmService;
    private final LogTicketService logTicketService;

    private final AgentRepo agentRepo;
    private final AgentService agentService;
    private final AgentQueryService agentQueryService;

    private final NotifierService notifierService;
    private final StorageService storageService;

    @Transactional
    public Ticket close(String ticketIdOrNo, TicketStatusFormDTO form) {
        log.info("CLOSE FORM {}", form);

        Ticket ticket = queryService.findByIdOrNo(ticketIdOrNo);
        TicketSummary summary = summaryQueryService.findByIdOrNo(ticketIdOrNo);

        if (!summary.isWip())
            throw BadRequestException.args("error.ticket.update.stat");
        else if (!summary.getWipBy().equals(SecurityUtil.getCurrentUser().getId()))
            throw BadRequestException.args("error.ticket.update.stat.agent");

        AgentWorkspace workspace = agentQueryService.getLastWorkspace(ticket.getId());
        Agent agent = workspace.getAgent();

        workspace.getLastWorklog().ifPresent(worklog -> {
            worklog.setCloseStatus(TcStatus.CLOSED);
            worklog.setSolution(form.getSolution());
            worklog.setMessage(form.getNote());
            agentService.save(worklog);

            storageService.addDashboardAssets(ticket, worklog, form.getFilesCollection());
        });

        int minute = appConfigService.getCloseConfirm_int()
                .getAsNumber()
                .intValue();

        int messageId = notifierService.sendCloseConfirmation(ticket, minute, form);
        ticket.setStatus(TcStatus.CONFIRMATION);
        ticket.setConfirmMessageId((long) messageId);

        confirmService.save(TicketConfirm.builder()
                .id(messageId)
                .value(ticket.getNo())
                .status(TicketConfirm.CLOSED)
                .ttl(minute)
                .build());

        log.info("NOTIF SENDED TO USER -- MESSAGE ID {}", messageId);
        logTicketService.add(LogTicket.builder()
                .ticket(ticket)
                .prev(TcStatus.PROGRESS)
                .curr(ticket.getStatus())
                .agentId(agent.getId())
                .message(LOG_CLOSE_CONFIRMATION)
                .build());

        return service.save(ticket);
    }

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

        AgentWorkspace workspace = agentQueryService.getLastWorkspace(ticket.getId());
        Agent agent = workspace.getAgent();

        if (reopen) {
            TcStatus prevStatus = ticket.getStatus();
            ticket.setStatus(TcStatus.REOPEN);
            ticket.setConfirmMessageId(null);

            String reopenMessage = StringUtils.isNoneBlank(form.getNote()) ?
                    form.getNote() : "<no description>";

            // Buat baru worklog
            AgentWorklog newWorklog = agentService.save(AgentWorklog.builder()
                    .takeStatus(TcStatus.REOPEN)
                    .workspace(workspace)
                    .reopenMessage(reopenMessage)
                    .build());
            storageService.addTelegramAssets(ticket, newWorklog, form.getPhotos(), "requestor");

            logTicketService.add(LogTicket.builder()
                    .ticket(ticket)
                    .prev(prevStatus)
                    .curr(ticket.getStatus())
                    .message(LOG_REOPEN)
                    .build());

            notifierService.safeSend(
                    agent.getTelegramId(),
                    "tg.ticket.confirm.reopen.agent",
                    ticket.getNo(),
                    reopenMessage);
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

                log.info("NOTIFY AGENT -- ID {}", agent.getUserId());
                notifierService.safeSend(agent.getTelegramId(),
                        "tg.ticket.confirm.closed.agent",
                        ticket.getNo(),
                        Translator.tr("app.done.watermark")
                );
            }
            else {
                logMessage = LOG_AUTO_CLOSE;
                notifierService.send(ticket.getSenderId(),
                        "tg.ticket.confirm.auto-closed",
                        ticket.getNo());
            }

            workspace.setStatus(AgStatus.CLOSED);
            agentService.save(workspace);

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
    public void confirmCloseAsync(String ticketIdOrNo, boolean reopen, TicketStatusFormDTO form) {
        confirmClose(ticketIdOrNo, reopen, form);
    }


}

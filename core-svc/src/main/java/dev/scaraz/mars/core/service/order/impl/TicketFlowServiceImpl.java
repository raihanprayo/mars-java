package dev.scaraz.mars.core.service.order.impl;

import dev.scaraz.mars.common.domain.request.TicketStatusFormDTO;
import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.tools.Translator;
import dev.scaraz.mars.common.tools.enums.AgStatus;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.core.domain.cache.CacheTicketConfirm;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.domain.order.LogTicket;
import dev.scaraz.mars.core.domain.order.Ticket;
import dev.scaraz.mars.core.domain.order.TicketAgent;
import dev.scaraz.mars.core.domain.view.TicketSummary;
import dev.scaraz.mars.core.query.TicketAgentQueryService;
import dev.scaraz.mars.core.query.TicketQueryService;
import dev.scaraz.mars.core.query.TicketSummaryQueryService;
import dev.scaraz.mars.core.query.criteria.TicketAgentCriteria;
import dev.scaraz.mars.core.repository.cache.CacheTicketConfirmRepo;
import dev.scaraz.mars.core.repository.order.TicketAgentRepo;
import dev.scaraz.mars.core.service.NotifierService;
import dev.scaraz.mars.core.service.StorageService;
import dev.scaraz.mars.core.service.order.LogTicketService;
import dev.scaraz.mars.core.service.order.TicketFlowService;
import dev.scaraz.mars.core.service.order.TicketService;
import dev.scaraz.mars.core.util.SecurityUtil;
import dev.scaraz.mars.telegram.UpdateContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor

@Service
public class TicketFlowServiceImpl implements TicketFlowService {

    private final TicketService service;
    private final TicketQueryService queryService;
    private final TicketSummaryQueryService summaryQueryService;

    private final TicketAgentRepo agentRepo;
    private final TicketAgentQueryService agentQueryService;

    private final CacheTicketConfirmRepo ticketConfirmRepo;

    private final NotifierService notifierService;
    private final StorageService storageService;

    private final LogTicketService logTicketService;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Ticket take(String ticketIdOrNo) {
        Ticket ticket = queryService.findByIdOrNo(ticketIdOrNo);

        if (summaryQueryService.isWorkInProgressByTicketId(ticket.getId()))
            throw BadRequestException.args("error.ticket.taken");

        User user = SecurityUtil.getCurrentUser();
        TicketAgent agent = agentRepo.save(TicketAgent.builder()
                .ticket(ticket)
                .user(user)
                .status(AgStatus.PROGRESS)
                .build());

        ticket.setStatus(TcStatus.PROGRESS);
        notifierService.sendTaken(ticket, user);

        logTicketService.add(LogTicket.builder()
                .ticket(ticket)
                .prev(TcStatus.OPEN)
                .curr(ticket.getStatus())
                .agent(agent)
                .message(LOG_WORK_IN_PROGRESS)
                .build());

        return service.save(ticket);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
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
                form.getNote());

        ticket.setStatus(TcStatus.CONFIRMATION);
        int messageId = notifierService.sendConfirmation(ticket);

        ticketConfirmRepo.save(CacheTicketConfirm.builder()
                .id(messageId)
                .no(ticket.getNo())
                .ttl(1800)
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
    @Transactional(propagation = Propagation.REQUIRES_NEW)
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
                form.getNote());

        if (form.getFiles() != null)
            storageService.addPhotoForAgentAsync(ticket, agent, List.of(form.getFiles()));

        ticket.setStatus(TcStatus.DISPATCH);
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
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Ticket confirm(String ticketIdOrNo, boolean reopen, TicketStatusFormDTO form) {
        Ticket ticket = queryService.findByIdOrNo(ticketIdOrNo);

        if (ticket.getStatus() != TcStatus.CONFIRMATION)
            throw BadRequestException.args("error.ticket.illegal.close.confirm.state");

        if (reopen) {
            ticket.setStatus(TcStatus.REOPEN);

            TicketAgent prevAgent = agentQueryService.findAll(
                    TicketAgentCriteria.builder()
                            .ticketId(new StringFilter().setEq(ticket.getId()))
                            .build(),
                    PageRequest.of(0, 1, Sort.Direction.DESC, "createdAt")
            ).stream().findFirst().orElseThrow();


            agentRepo.save(TicketAgent.builder()
                    .status(AgStatus.PROGRESS)
                    .user(prevAgent.getUser())
                    .ticket(ticket)
                    .build());

            logTicketService.add(LogTicket.builder()
                    .ticket(ticket)
                    .prev(TcStatus.CONFIRMATION)
                    .curr(ticket.getStatus())
                    .message(LOG_REOPEN)
                    .build());
        }
        else {
            ticket.setStatus(TcStatus.CLOSED);

            String message;
            if (UpdateContextHolder.hasUpdate()) message = LOG_CONFIRMED_CLOSE;
            else {
                message = LOG_AUTO_CLOSE;
                notifierService.send(ticket.getSenderId(),
                        "tg.ticket.notice.auto-close",
                        ticket.getNo()
                );
            }

            logTicketService.add(LogTicket.builder()
                    .ticket(ticket)
                    .prev(TcStatus.CONFIRMATION)
                    .curr(ticket.getStatus())
                    .message(message)
                    .build());
        }


        return service.save(ticket);
    }

}

package dev.scaraz.mars.core.service.order.flow;

import dev.scaraz.mars.common.domain.request.TicketStatusFormDTO;
import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.tools.enums.AgStatus;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.core.domain.order.AgentWorkspace;
import dev.scaraz.mars.core.domain.order.LogTicket;
import dev.scaraz.mars.core.domain.order.Ticket;
import dev.scaraz.mars.core.domain.order.Agent;
import dev.scaraz.mars.core.domain.view.TicketSummary;
import dev.scaraz.mars.core.query.AgentQueryService;
import dev.scaraz.mars.core.query.TicketQueryService;
import dev.scaraz.mars.core.query.TicketSummaryQueryService;
import dev.scaraz.mars.core.repository.order.AgentRepo;
import dev.scaraz.mars.core.service.NotifierService;
import dev.scaraz.mars.core.service.StorageService;
import dev.scaraz.mars.core.service.order.AgentService;
import dev.scaraz.mars.core.service.order.LogTicketService;
import dev.scaraz.mars.core.service.order.TicketService;
import dev.scaraz.mars.core.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor

@Service
public class DispatchFlowService {

    private final TicketService service;
    private final TicketQueryService queryService;
    private final TicketSummaryQueryService summaryQueryService;
    private final LogTicketService logTicketService;

    private final AgentService agentService;
    private final AgentQueryService agentQueryService;

    private final NotifierService notifierService;
    private final StorageService storageService;

    @Transactional
    public Ticket dispatch(String ticketIdOrNo, TicketStatusFormDTO form) {
        Ticket ticket = queryService.findByIdOrNo(ticketIdOrNo);
        TicketSummary summary = summaryQueryService.findByIdOrNo(ticketIdOrNo);

        if (!summary.isWip())
            throw BadRequestException.args("error.ticket.update.stat");
        else if (!summary.getWipBy().getId().equals(SecurityUtil.getCurrentUser().getId()))
            throw BadRequestException.args("error.ticket.update.stat.agent");

        AgentWorkspace workspace = agentQueryService.getLastWorkspace(ticket.getId());
        Agent agent = workspace.getAgent();

        workspace.getLastWorklog().ifPresent(worklog -> {
            worklog.setCloseStatus(TcStatus.DISPATCH);
            worklog.setSolution(form.getSolution());
            worklog.setMessage(form.getNote());
            workspace.setStatus(AgStatus.CLOSED);

            agentService.save(workspace);
        });

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
                .agentId(agent.getId())
                .message(LogTicketService.LOG_DISPATCH_REQUEST)
                .build());
        return service.save(ticket);
    }

}
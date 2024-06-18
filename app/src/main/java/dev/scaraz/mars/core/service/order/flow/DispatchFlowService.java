package dev.scaraz.mars.core.service.order.flow;

import dev.scaraz.mars.common.domain.request.TicketStatusFormDTO;
import dev.scaraz.mars.common.tools.enums.AgStatus;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.domain.order.*;
import dev.scaraz.mars.core.domain.symptom.Solution;
import dev.scaraz.mars.core.query.*;
import dev.scaraz.mars.core.service.NotifierService;
import dev.scaraz.mars.core.service.StorageService;
import dev.scaraz.mars.core.service.order.AgentService;
import dev.scaraz.mars.core.service.order.LogTicketService;
import dev.scaraz.mars.core.service.order.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor

@Service
public class DispatchFlowService {

    private final TicketService service;
    private final TicketQueryService queryService;
    private final TicketSummaryQueryService summaryQueryService;
    private final LogTicketService logTicketService;

    private final AgentService agentService;
    //    private final AgentQueryService agentQueryService;
    private final AgentWorkspaceQueryService agentWorkspaceQueryService;
    private final AgentWorklogQueryService agentWorklogQueryService;

    private final SolutionQueryService solutionQueryService;

    private final NotifierService notifierService;
    private final StorageService storageService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Ticket dispatch(String ticketIdOrNo, TicketStatusFormDTO form) {
        Ticket ticket = queryService.findByIdOrNo(ticketIdOrNo);

//        if (!summary.isWip())
//            throw BadRequestException.args("error.ticket.update.stat");
//        else if (!summary.getWipBy().equals(SecurityUtil.getCurrentUser().getId()))
//            throw BadRequestException.args("error.ticket.update.stat.agent");

        AgentWorkspace workspace = agentWorkspaceQueryService.getLastWorkspace(ticket.getId());
        Account agent = workspace.getAccount();

        workspace.setStatus(AgStatus.CLOSED);
        workspace.getLastWorklog().ifPresent(worklog -> {
            worklog.setCloseStatus(TcStatus.DISPATCH);
            worklog.setMessage(form.getNote());

            if (form.getSolution() != null) {
                Solution solution = solutionQueryService.findById(form.getSolution());
                worklog.setSolution(new WlSolution(solution));
            }

            storageService.addDashboardAssets(ticket, worklog, form.getFilesCollection());
        });
        agentService.save(workspace);

//        if (form.getFiles() != null)
//            storageService.addPhotoForAgentAsync(ticket, agent, List.of(form.getFiles()));

        ticket.setStatus(TcStatus.DISPATCH);

        log.info("NOTIFY SENDER -- ID {}", ticket.getSenderId());
        notifierService.safeSend(ticket.getSenderId(),
                "tg.ticket.update.dispatch",
                ticket.getNo());

        if (agent.getTg().getId() != ticket.getSenderId()) {
            log.info("NOTIFY AGENT -- ID {}", agent.getTg().getId());
            notifierService.safeSend(agent.getTg().getId(),
                    "tg.ticket.update.dispatch.agent",
                    ticket.getNo());
        }

        logTicketService.add(LogTicket.builder()
                .ticket(ticket)
                .prev(TcStatus.PROGRESS)
                .curr(ticket.getStatus())
                .userId(agent.getId())
                .message(LogTicketService.LOG_DISPATCH_REQUEST)
                .build());
        return service.save(ticket);
    }

}

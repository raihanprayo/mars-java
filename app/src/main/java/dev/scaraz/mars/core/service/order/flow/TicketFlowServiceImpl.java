package dev.scaraz.mars.core.service.order.flow;

import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.exception.web.InternalServerException;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.common.utils.AuthorityConstant;
import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.domain.agent.AgentWorklog;
import dev.scaraz.mars.core.domain.agent.AgentWorkspace;
import dev.scaraz.mars.core.domain.order.LogTicket;
import dev.scaraz.mars.core.domain.order.Ticket;
import dev.scaraz.mars.core.query.AccountQueryService;
import dev.scaraz.mars.core.query.AgentWorklogQueryService;
import dev.scaraz.mars.core.query.AgentWorkspaceQueryService;
import dev.scaraz.mars.core.query.TicketQueryService;
import dev.scaraz.mars.core.service.NotifierService;
import dev.scaraz.mars.core.service.order.AgentService;
import dev.scaraz.mars.core.service.order.LogTicketService;
import dev.scaraz.mars.core.service.order.TicketFlowService;
import dev.scaraz.mars.core.service.order.TicketService;
import dev.scaraz.mars.security.MarsUserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor

@Service
public class TicketFlowServiceImpl implements TicketFlowService {

    //    private final AppConfigService appConfigService;
    private final TicketService service;
    private final TicketQueryService queryService;

    private final AgentService agentService;
//    private final AgentQueryService agentQueryService;
    private final AgentWorkspaceQueryService agentWorkspaceQueryService;
    private final AgentWorklogQueryService agentWorklogQueryService;

    private final NotifierService notifierService;
    private final LogTicketService logTicketService;

    private final AccountQueryService accountQueryService;

    @Override
    @Transactional
    public Ticket take(String ticketIdOrNo) {
        if (!MarsUserContext.hasAnyRole(AuthorityConstant.AGENT_ROLE))
            throw BadRequestException.args("Anda tidak mempunyai akses untuk memproses tiket");

        // Pengambilan tiket dengan kondisi tiket belum dikerjakan
        Ticket ticket = queryService.findByIdOrNo(ticketIdOrNo);

        if (!List.of(TcStatus.OPEN, TcStatus.DISPATCH).contains(ticket.getStatus()))
            throw BadRequestException.args("Status pengambilan tiket invalid");
        else if (agentWorkspaceQueryService.isWorkInProgress(ticket.getId()))
            throw new BadRequestException("Tidak dapat mengambil Order/Tiket yang sedang dalam pengerjaan");

        if (MarsUserContext.isUserPresent()) {
            Account account = accountQueryService.findByCurrentAccess();
            TcStatus prevStatus = ticket.getStatus();

            AgentWorkspace workspace = agentService.getWorkspaceByCurrentUser(ticket.getId());
            Account agent = workspace.getAccount();

            agentService.save(AgentWorklog.builder()
                    .takeStatus(prevStatus)
                    .workspace(workspace)
                    .build());

            ticket.setStatus(TcStatus.PROGRESS);

            boolean isPreviousStatusOpen = prevStatus == TcStatus.OPEN;
            if (isPreviousStatusOpen) notifierService.sendTaken(ticket, account.getName());
            else notifierService.sendRetaken(ticket, account.getName());

            logTicketService.add(LogTicket.builder()
                    .ticket(ticket)
                    .prev(prevStatus)
                    .curr(ticket.getStatus())
                    .userId(agent.getId())
                    .message(isPreviousStatusOpen ? LogTicketService.LOG_WORK_IN_PROGRESS : LogTicketService.LOG_REWORK_IN_PROGRESS)
                    .build());

            return service.save(ticket);
        }

        throw InternalServerException.args("akses akun tidak ditemukan");
    }

}

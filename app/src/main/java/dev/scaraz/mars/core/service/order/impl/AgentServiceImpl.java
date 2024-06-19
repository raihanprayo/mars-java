package dev.scaraz.mars.core.service.order.impl;

import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.exception.web.InternalServerException;
import dev.scaraz.mars.common.tools.enums.AgStatus;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.common.utils.AuthorityConstant;
import dev.scaraz.mars.core.domain.agent.Agent;
import dev.scaraz.mars.core.domain.agent.AgentWorklog;
import dev.scaraz.mars.core.domain.agent.AgentWorkspace;
import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.domain.order.*;
import dev.scaraz.mars.core.domain.symptom.Solution;
import dev.scaraz.mars.core.query.AccountQueryService;
import dev.scaraz.mars.core.query.AgentWorklogQueryService;
import dev.scaraz.mars.core.query.AgentWorkspaceQueryService;
import dev.scaraz.mars.core.query.criteria.AgentWorkspaceCriteria;
import dev.scaraz.mars.core.query.criteria.TicketCriteria;
import dev.scaraz.mars.core.repository.db.order.AgentRepo;
import dev.scaraz.mars.core.repository.db.order.AgentWorklogRepo;
import dev.scaraz.mars.core.repository.db.order.AgentWorkspaceRepo;
import dev.scaraz.mars.core.service.order.AgentService;
import dev.scaraz.mars.security.MarsUserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor

@Service
public class AgentServiceImpl implements AgentService {


    private final AgentRepo repo;
    private final AgentWorkspaceRepo workspaceRepo;
    private final AgentWorklogRepo worklogRepo;

    private final AgentWorkspaceQueryService workspaceQueryService;
    private final AgentWorklogQueryService worklogQueryService;

    private final AccountQueryService accountQueryService;

    @Override
    public Agent save(Agent agent) {
        return repo.save(agent);
    }

    @Override
    public AgentWorkspace save(AgentWorkspace workspace) {
        return workspaceRepo.save(workspace);
    }

    @Override
    public AgentWorklog save(AgentWorklog worklog) {
        return worklogRepo.save(worklog);
    }

    @Override
    public AgentWorkspace getWorkspace(String ticketId, String agentId) {
        Supplier<AgentWorkspace> fn = () -> save(AgentWorkspace.builder()
                .ticket(Ticket.builder().id(ticketId).build())
                .account(Account.builder().id(agentId).build())
                .build());

        return workspaceRepo.findFirstByTicketIdAndAccountIdOrderByCreatedAtDesc(ticketId, agentId)
                .map(workspace -> workspace.getStatus() == AgStatus.CLOSED ? fn.get() : workspace)
                .orElseGet(fn);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AgentWorkspace getWorkspaceByCurrentUser(String ticketId) {
        Account account = accountQueryService.findById(MarsUserContext.getId());

        if (account == null) throw InternalServerException.args("Data akses user tidak ada!");
        else if (!account.hasAnyRole(AuthorityConstant.AGENT_ROLE))
            throw new BadRequestException("User tidak punya akses sebagai agent");
        else if (account.getTg().getId() == null)
            throw new BadRequestException("Agent tidak punya telegram id, untuk melakukan notifikasi");

//        Agent agent = repo.findByUserId(account.getId())
//                .map(a -> {
//                    if (!a.getNik().equals(account.getNik())) a.setNik(account.getNik());
//                    if (!a.getTelegramId().equals(account.getTg().getId())) a.setTelegramId(account.getTg().getId());
//                    return save(a);
//                })
//                .orElseGet(() -> save(Agent.builder()
//                        .nik(account.getNik())
//                        .telegramId(account.getTg().getId())
//                        .userId(account.getId())
//                        .build()));

        Supplier<AgentWorkspace> fn = () -> save(AgentWorkspace.builder()
                .account(account)
                .ticket(Ticket.builder().id(ticketId).build())
                .status(AgStatus.PROGRESS)
                .build());

        return workspaceRepo.findFirstByTicketIdAndAccountIdOrderByCreatedAtDesc(ticketId, account.getId())
                .map(workspace -> workspace.getStatus() != AgStatus.CLOSED ? workspace : fn.get())
                .orElseGet(fn);
    }

    @Async
    @Override
    @Transactional
    public void updateWorlklogSolution(Solution solution) {
        worklogRepo.updateSolutionBySolutionId(
                solution.getId(),
                solution.getName(),
                solution.getDescription()
        );
    }

    @Override
    @Transactional
    public void deleteAllWorkspaceByTicketId(String ticketId) {
        List<AgentWorkspace> workspaces = workspaceQueryService.findAll(new AgentWorkspaceCriteria().setTicket(new TicketCriteria()
                .setId(new StringFilter().setEq(ticketId))));
        workspaceRepo.deleteAll(workspaces);
    }

}

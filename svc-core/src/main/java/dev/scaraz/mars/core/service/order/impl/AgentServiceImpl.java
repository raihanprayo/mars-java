package dev.scaraz.mars.core.service.order.impl;

import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.exception.web.InternalServerException;
import dev.scaraz.mars.common.tools.enums.AgStatus;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.domain.order.Agent;
import dev.scaraz.mars.core.domain.order.AgentWorklog;
import dev.scaraz.mars.core.domain.order.AgentWorkspace;
import dev.scaraz.mars.core.domain.order.Ticket;
import dev.scaraz.mars.core.query.AccountQueryService;
import dev.scaraz.mars.core.repository.db.order.AgentRepo;
import dev.scaraz.mars.core.repository.db.order.AgentWorklogRepo;
import dev.scaraz.mars.core.repository.db.order.AgentWorkspaceRepo;
import dev.scaraz.mars.core.service.order.AgentService;
import dev.scaraz.mars.security.MarsUserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor

@Service
public class AgentServiceImpl implements AgentService {


    private final AgentRepo repo;
    private final AgentWorkspaceRepo workspaceRepo;
    private final AgentWorklogRepo worklogRepo;

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
                .agent(Agent.builder().id(agentId).build())
                .build());

        return workspaceRepo.findFirstByTicketIdAndAgentIdOrderByCreatedAtDesc(ticketId, agentId)
                .map(workspace -> workspace.getStatus() == AgStatus.CLOSED ? fn.get() : workspace)
                .orElseGet(fn);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AgentWorkspace getWorkspaceByCurrentUser(String ticketId) {
        Account account = accountQueryService.findById(MarsUserContext.getId());

        if (account == null) throw InternalServerException.args("Data akses user tidak ada!");
        else if (!account.hasAnyRole(AppConstants.Authority.AGENT_ROLE))
            throw new BadRequestException("User tidak punya akses sebagai agent");
        else if (account.getTg().getId() == null)
            throw new BadRequestException("Agent tidak punya telegram id, untuk melakukan notifikasi");

        Agent agent = repo.findByUserId(account.getId())
                .map(a -> {
                    if (!a.getNik().equals(account.getNik())) a.setNik(account.getNik());
                    if (!a.getTelegramId().equals(account.getTg().getId())) a.setTelegramId(account.getTg().getId());
                    return save(a);
                })
                .orElseGet(() -> save(Agent.builder()
                        .nik(account.getNik())
                        .telegramId(account.getTg().getId())
                        .userId(account.getId())
                        .build()));

        Supplier<AgentWorkspace> fn = () -> save(AgentWorkspace.builder()
                .agent(agent)
                .ticket(Ticket.builder().id(ticketId).build())
                .status(AgStatus.PROGRESS)
                .build());

        return workspaceRepo.findFirstByTicketIdAndAgentIdOrderByCreatedAtDesc(ticketId, agent.getId())
                .map(workspace -> workspace.getStatus() != AgStatus.CLOSED ? workspace : fn.get())
                .orElseGet(fn);
    }

}

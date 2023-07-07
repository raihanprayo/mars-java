package dev.scaraz.mars.v1.core.service.order.impl;

import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.exception.web.InternalServerException;
import dev.scaraz.mars.common.tools.enums.AgStatus;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.v1.core.domain.credential.User;
import dev.scaraz.mars.v1.core.domain.order.Agent;
import dev.scaraz.mars.v1.core.domain.order.AgentWorklog;
import dev.scaraz.mars.v1.core.domain.order.AgentWorkspace;
import dev.scaraz.mars.v1.core.domain.order.Ticket;
import dev.scaraz.mars.v1.core.repository.order.AgentRepo;
import dev.scaraz.mars.v1.core.repository.order.AgentWorklogRepo;
import dev.scaraz.mars.v1.core.repository.order.AgentWorkspaceRepo;
import dev.scaraz.mars.v1.core.service.order.AgentService;
import dev.scaraz.mars.v1.core.util.SecurityUtil;
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
        User user = SecurityUtil.getCurrentUser();

        if (user == null) throw InternalServerException.args("Data akses user tidak ada!");
        else if (!user.hasAnyRole(AppConstants.Authority.AGENT_ROLE))
            throw new BadRequestException("User tidak punya akses sebagai agent");
        else if (user.getTg().getId() == null)
            throw new BadRequestException("Agent tidak punya telegram id, untuk melakukan notifikasi");

        Agent agent = repo.findByUserId(user.getId())
                .map(a -> {
                    if (!a.getNik().equals(user.getNik())) a.setNik(user.getNik());
                    if (!a.getTelegramId().equals(user.getTg().getId())) a.setTelegramId(user.getTg().getId());
                    return save(a);
                })
                .orElseGet(() -> save(Agent.builder()
                        .nik(user.getNik())
                        .telegramId(user.getTg().getId())
                        .userId(user.getId())
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

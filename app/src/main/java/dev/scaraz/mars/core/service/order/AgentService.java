package dev.scaraz.mars.core.service.order;

import dev.scaraz.mars.core.domain.order.Agent;
import dev.scaraz.mars.core.domain.order.AgentWorklog;
import dev.scaraz.mars.core.domain.order.AgentWorkspace;
import dev.scaraz.mars.core.domain.order.Solution;
import org.springframework.transaction.annotation.Transactional;

public interface AgentService {
    Agent save(Agent agent);

    AgentWorkspace save(AgentWorkspace workspace);

    AgentWorklog save(AgentWorklog worklog);

    AgentWorkspace getWorkspace(String ticketId, String agentId);

    AgentWorkspace getWorkspaceByCurrentUser(String ticketId);

    @Transactional
    void updateWorlklogSolution(Solution solution);

    void deleteAllWorkspaceByTicketId(String ticketId);
}

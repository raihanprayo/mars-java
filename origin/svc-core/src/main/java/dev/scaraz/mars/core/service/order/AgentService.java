package dev.scaraz.mars.core.service.order;

import dev.scaraz.mars.core.domain.order.Agent;
import dev.scaraz.mars.core.domain.order.AgentWorklog;
import dev.scaraz.mars.core.domain.order.AgentWorkspace;

public interface AgentService {
    Agent save(Agent agent);

    AgentWorkspace save(AgentWorkspace workspace);

    AgentWorklog save(AgentWorklog worklog);

    AgentWorkspace getWorkspace(String ticketId, String agentId);

    AgentWorkspace getWorkspaceByCurrentUser(String ticketId);
}

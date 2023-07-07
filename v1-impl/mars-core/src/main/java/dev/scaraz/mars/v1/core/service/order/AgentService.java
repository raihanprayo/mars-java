package dev.scaraz.mars.v1.core.service.order;

import dev.scaraz.mars.v1.core.domain.order.Agent;
import dev.scaraz.mars.v1.core.domain.order.AgentWorklog;
import dev.scaraz.mars.v1.core.domain.order.AgentWorkspace;

public interface AgentService {
    Agent save(Agent agent);

    AgentWorkspace save(AgentWorkspace workspace);

    AgentWorklog save(AgentWorklog worklog);

    AgentWorkspace getWorkspace(String ticketId, String agentId);

    AgentWorkspace getWorkspaceByCurrentUser(String ticketId);
}

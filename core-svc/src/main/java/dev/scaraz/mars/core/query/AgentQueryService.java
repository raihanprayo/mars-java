package dev.scaraz.mars.core.query;

import dev.scaraz.mars.core.domain.order.Agent;
import dev.scaraz.mars.core.domain.order.AgentWorkspace;
import dev.scaraz.mars.core.query.criteria.AgentCriteria;

import java.util.List;
import java.util.Optional;

public interface AgentQueryService extends BaseQueryService<Agent, AgentCriteria> {

    AgentWorkspace getLastWorkspace(String ticketId);

    boolean isWorkInProgress(String ticketId);
}

package dev.scaraz.mars.core.query;

import dev.scaraz.mars.common.exception.web.NotFoundException;
import dev.scaraz.mars.core.domain.order.AgentWorkspace;
import dev.scaraz.mars.core.query.criteria.AgentWorkspaceCriteria;

import java.util.List;
import java.util.Optional;

public interface AgentWorkspaceQueryService extends BaseQueryService<AgentWorkspace, AgentWorkspaceCriteria> {
    List<AgentWorkspace> findWorkspacesByTicket(String ticketIdOrNo);

    AgentWorkspace getLastWorkspace(String ticketId);

    AgentWorkspace getLastWorkspace(String ticketId, boolean bypass) throws NotFoundException;

    Optional<AgentWorkspace> getLastWorkspaceOptional(String ticketId);

    Optional<AgentWorkspace> getLastWorkspaceOptional(String ticketId, boolean bypass);

    boolean isWorkInProgress(String ticketId);
}

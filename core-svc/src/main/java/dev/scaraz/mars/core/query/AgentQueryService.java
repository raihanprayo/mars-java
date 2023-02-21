package dev.scaraz.mars.core.query;

import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.exception.web.NotFoundException;
import dev.scaraz.mars.core.domain.order.Agent;
import dev.scaraz.mars.core.domain.order.AgentWorklog;
import dev.scaraz.mars.core.domain.order.AgentWorkspace;
import dev.scaraz.mars.core.query.criteria.AgentCriteria;

import java.util.List;

public interface AgentQueryService extends BaseQueryService<Agent, AgentCriteria> {

    List<AgentWorkspace> findByUserId(String userId);

    List<AgentWorkspace> findWorkspacesByTicket(String ticketIdOrNo);

    List<AgentWorklog> findWorklogsByTicketIdOrNo(String ticketIdOrNo);

    AgentWorkspace getLastWorkspace(String ticketId) throws BadRequestException, NotFoundException;

    boolean isWorkInProgress(String ticketId);
}

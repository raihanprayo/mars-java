package dev.scaraz.mars.core.query;

import dev.scaraz.mars.core.domain.order.AgentWorklog;
import dev.scaraz.mars.core.query.criteria.AgentWorklogCriteria;

import java.util.List;

public interface AgentWorklogQueryService extends BaseQueryService<AgentWorklog, AgentWorklogCriteria> {
    List<AgentWorklog> findWorklogsByTicketIdOrNo(String ticketIdOrNo);
}

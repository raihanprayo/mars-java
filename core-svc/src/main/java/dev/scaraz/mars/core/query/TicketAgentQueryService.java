package dev.scaraz.mars.core.query;

import dev.scaraz.mars.core.domain.order.TicketAgent;
import dev.scaraz.mars.core.query.criteria.TicketAgentCriteria;

import java.util.List;
import java.util.Optional;

public interface TicketAgentQueryService extends BaseQueryService<TicketAgent, TicketAgentCriteria> {
    Optional<TicketAgent> findOne(TicketAgentCriteria criteria);

    boolean hasAgentInProgressByTicketId(String ticketId);

    boolean hasAgentInProgressByTicketNo(String ticketIdOrNo);

    List<TicketAgent> findByTicketId(String ticketId);
}

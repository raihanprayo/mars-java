package dev.scaraz.mars.core.query;

import dev.scaraz.mars.core.domain.order.Ticket;
import dev.scaraz.mars.core.query.criteria.TicketCriteria;

public interface TicketQueryService extends BaseQueryService<Ticket, TicketCriteria> {
    Ticket findById(String id);

    Ticket findByIdOrNo(String idOrNo);

    int countByServiceNo(String serviceNo);
}

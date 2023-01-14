package dev.scaraz.mars.core.query;

import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.core.domain.order.Ticket;
import dev.scaraz.mars.core.query.criteria.TicketAgentCriteria;
import dev.scaraz.mars.core.query.criteria.TicketCriteria;

import javax.annotation.Nullable;
import java.util.Map;

public interface TicketQueryService extends BaseQueryService<Ticket, TicketCriteria> {
    Ticket findById(String id);

    Ticket findByIdOrNo(String idOrNo);

    Ticket findByMessageId(Long messageId);

    long count(TicketCriteria criteria);

    Map<Product, Long> countProducts(@Nullable TicketAgentCriteria agentCriteria);

    int countGaul(String issueId, String serviceNo);
}

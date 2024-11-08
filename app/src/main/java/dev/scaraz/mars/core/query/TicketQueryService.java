package dev.scaraz.mars.core.query;

import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.core.domain.order.Ticket;
import dev.scaraz.mars.core.query.criteria.AgentCriteria;
import dev.scaraz.mars.core.query.criteria.TicketCriteria;

import jakarta.annotation.Nullable;

import java.util.Collection;
import java.util.Map;

public interface TicketQueryService extends BaseQueryService<Ticket, TicketCriteria> {
    Ticket findById(String id);

    Ticket findByIdOrNo(String idOrNo);

    Ticket findByMessageId(Long messageId);

    long count(TicketCriteria criteria);

    boolean exist(TicketCriteria criteria);

    Map<Product, Long> countProducts(@Nullable AgentCriteria agentCriteria);

    int countGaul(long issueId, String serviceNo);

    double sumTotalScore(Collection<String> ids);
}

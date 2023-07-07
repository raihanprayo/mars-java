package dev.scaraz.mars.v1.core.query;

import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.v1.core.domain.view.TicketSummary;
import dev.scaraz.mars.v1.core.query.criteria.TicketSummaryCriteria;

import java.time.Instant;
import java.util.List;

public interface TicketSummaryQueryService extends BaseQueryService<TicketSummary, TicketSummaryCriteria> {

    List<TicketSummary> getGaulRelatedByIdOrNo(String tcIdOrNo);

    List<String> getAllIds(Instant from, Instant to);

    TicketSummary findByIdOrNo(String id);

    long countByProduct(Product product, boolean currentUser);

    boolean isWorkInProgressByTicketId(String ticketId);
    boolean isWorkInProgressByTicketId(String ticketId, boolean byCurrentUser);
}

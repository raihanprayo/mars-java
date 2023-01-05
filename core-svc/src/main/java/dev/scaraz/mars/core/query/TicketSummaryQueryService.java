package dev.scaraz.mars.core.query;

import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.core.domain.view.TicketSummary;
import dev.scaraz.mars.core.query.criteria.TicketSummaryCriteria;

public interface TicketSummaryQueryService extends BaseQueryService<TicketSummary, TicketSummaryCriteria> {

    TicketSummary findByIdOrNo(String id);

    long countByProduct(Product product, boolean currentUser);

    boolean isWorkInProgressByTicketId(String ticketId);
    boolean isWorkInProgressByTicketId(String ticketId, boolean byCurrentUser);
}

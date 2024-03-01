package dev.scaraz.mars.core.query;

import dev.scaraz.mars.core.domain.view.WorklogSummary;
import dev.scaraz.mars.core.query.criteria.WorklogSummaryCriteria;

import java.util.List;

public interface WorklogSummaryQueryService {
    List<WorklogSummary> findAll(WorklogSummaryCriteria criteria);

    List<WorklogSummary> findAllByTicketId(String ticketId);
}

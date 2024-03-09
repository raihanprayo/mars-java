package dev.scaraz.mars.core.query.impl;

import dev.scaraz.mars.core.domain.view.WorklogSummary;
import dev.scaraz.mars.core.query.WorklogSummaryQueryService;
import dev.scaraz.mars.core.query.criteria.WorklogSummaryCriteria;
import dev.scaraz.mars.core.query.spec.WorklogSummarySpecBuilder;
import dev.scaraz.mars.core.repository.db.view.WorklogSummaryRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorklogSummaryQueryServiceImpl implements WorklogSummaryQueryService {

    private final WorklogSummaryRepo repo;
    private final WorklogSummarySpecBuilder specBuilder;

    @Override
    public List<WorklogSummary> findAll(WorklogSummaryCriteria criteria) {
        return repo.findAll(specBuilder.createSpec(criteria));
    }

    @Override
    public List<WorklogSummary> findAllByTicketId(String ticketId) {
        return repo.findAllByTicketIdOrderByCreatedAtDesc(ticketId);
    }

}

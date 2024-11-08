package dev.scaraz.mars.core.query.impl;

import dev.scaraz.mars.common.exception.web.NotFoundException;
import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.common.tools.filter.type.BooleanFilter;
import dev.scaraz.mars.common.tools.filter.type.InstantFilter;
import dev.scaraz.mars.common.tools.filter.type.LongFilter;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.core.domain.order.Ticket;
import dev.scaraz.mars.core.domain.view.TicketSummary;
import dev.scaraz.mars.core.query.AccountQueryService;
import dev.scaraz.mars.core.query.TicketSummaryQueryService;
import dev.scaraz.mars.core.query.criteria.IssueCriteria;
import dev.scaraz.mars.core.query.criteria.TicketSummaryCriteria;
import dev.scaraz.mars.core.query.spec.TicketSummarySpecBuilder;
import dev.scaraz.mars.core.repository.db.order.TicketSummaryRepo;
import dev.scaraz.mars.security.MarsUserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import static dev.scaraz.mars.common.utils.AppConstants.ZONE_LOCAL;

@Slf4j
@RequiredArgsConstructor

@Service
@Transactional(readOnly = true)
public class TicketSummaryQueryServiceImpl implements TicketSummaryQueryService {

    private final TicketSummaryRepo repo;
    private final TicketSummarySpecBuilder specBuilder;
    private final AccountQueryService accountQueryService;

    @Override
    public List<TicketSummary> findAll() {
        return repo.findAll();
    }

    @Override
    public Page<TicketSummary> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    public List<TicketSummary> findAll(TicketSummaryCriteria criteria) {
        return repo.findAll(specBuilder.createSpec(criteria));
    }

    @Override
    public Page<TicketSummary> findAll(TicketSummaryCriteria criteria, Pageable pageable) {
        return repo.findAll(specBuilder.createSpec(criteria), pageable);
    }

    @Override
    public List<TicketSummary> getGaulRelatedByIdOrNo(String tcIdOrNo) {
        TicketSummary tc = findByIdOrNo(tcIdOrNo);
        Instant createdAt = tc.getCreatedAt()
                .minusMillis(1);

        Instant weekAgo = createdAt.atZone(ZONE_LOCAL)
                .minusDays(7)
                .toInstant();

        return findAll((TicketSummaryCriteria) new TicketSummaryCriteria()
                .setId(new StringFilter().setNegated(true).setEq(tc.getId()))
                .setServiceNo(new StringFilter().setEq(tc.getServiceNo()))
                .setIssue(IssueCriteria.builder()
                        .id(new LongFilter().setEq(tc.getIssue().getId()))
                        .build())
                .setCreatedAt(new InstantFilter()
                        .setGte(weekAgo)
                        .setLte(createdAt)));
    }

    @Override
    public List<String> getAllIds(Instant from, Instant to) {
        return repo.getAllIds(from, to);
    }

    @Override
    public TicketSummary findByIdOrNo(String id) {
        return repo.findByIdOrNo(id, id)
                .orElseThrow(() -> NotFoundException.entity(Ticket.class, "id/no", id));
    }

    @Override
    public long count() {
        return repo.count();
    }

    @Override
    public long count(TicketSummaryCriteria criteria) {
        return repo.count(specBuilder.createSpec(criteria));
    }

    @Override
    public long countByProduct(Product product, boolean currentUser) {
        if (currentUser && MarsUserContext.isUserPresent()) {
            return repo.countByIssueProductAndWipBy(product, MarsUserContext.getId());
        }
        return repo.countByIssueProductAndWipIsFalse(product);
    }

    @Override
    public boolean isWorkInProgressByTicketId(String ticketId) {
        return isWorkInProgressByTicketId(ticketId, false);
    }

    @Override
    public boolean isWorkInProgressByTicketId(String ticketId, boolean currentUser) {
        TicketSummaryCriteria criteria = new TicketSummaryCriteria()
                .setId(new StringFilter().setEq(ticketId))
                .setWip(new BooleanFilter().setEq(true));

        if (currentUser && MarsUserContext.isUserPresent()) {
            criteria.setWipBy(new StringFilter().setEq(MarsUserContext.getId()));
        }

        return repo.exists(specBuilder.createSpec(criteria));
    }

}

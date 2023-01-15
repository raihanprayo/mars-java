package dev.scaraz.mars.core.query.impl;

import dev.scaraz.mars.common.exception.web.NotFoundException;
import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.common.tools.filter.type.BooleanFilter;
import dev.scaraz.mars.common.tools.filter.type.InstantFilter;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.domain.order.Ticket;
import dev.scaraz.mars.core.domain.view.TicketSummary;
import dev.scaraz.mars.core.query.TicketSummaryQueryService;
import dev.scaraz.mars.core.query.criteria.IssueCriteria;
import dev.scaraz.mars.core.query.criteria.TicketSummaryCriteria;
import dev.scaraz.mars.core.query.criteria.UserCriteria;
import dev.scaraz.mars.core.query.spec.TicketSummarySpecBuilder;
import dev.scaraz.mars.core.repository.order.TicketSummaryRepo;
import dev.scaraz.mars.core.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@RequiredArgsConstructor

@Service
@Transactional(readOnly = true)
public class TicketSummaryQueryServiceImpl implements TicketSummaryQueryService {

    private final TicketSummaryRepo repo;
    private final TicketSummarySpecBuilder specBuilder;

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
                .minusSeconds(1);

        Instant weekAgo = createdAt.atZone(ZoneId.of("Asia/Jakarta"))
                .minusDays(7)
                .toInstant();

        return findAll(TicketSummaryCriteria.builder()
                .id(new StringFilter().setNegated(true).setEq(tc.getId()))
                .serviceNo(new StringFilter().setEq(tc.getServiceNo()))
                .issue(IssueCriteria.builder()
                        .id(new StringFilter().setEq(tc.getIssue().getId()))
                        .build())
                .createdAt(new InstantFilter()
                        .setGte(weekAgo)
                        .setLte(createdAt))
                .build());
    }

    @Override
    public List<String> getAllByIds(Instant from, Instant to) {
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
        if (currentUser) {
            User usr = SecurityUtil.getCurrentUser();
            if (usr != null) return repo.countByProductAndWipById(product, usr.getId());
        }
        return repo.countByProductAndWipIsFalse(product);
    }

    @Override
    public boolean isWorkInProgressByTicketId(String ticketId) {
        return isWorkInProgressByTicketId(ticketId, false);
    }

    @Override
    public boolean isWorkInProgressByTicketId(String ticketId, boolean currentUser) {
        TicketSummaryCriteria criteria = TicketSummaryCriteria.builder()
                .id(new StringFilter().setEq(ticketId))
                .wip(new BooleanFilter().setEq(true))
                .build();

        if (currentUser) {
            User user = SecurityUtil.getCurrentUser();
            criteria.setWipBy(UserCriteria.builder()
                    .id(new StringFilter().setEq(user.getId()))
                    .build());
        }

        return repo.exists(specBuilder.createSpec(criteria));
    }

}

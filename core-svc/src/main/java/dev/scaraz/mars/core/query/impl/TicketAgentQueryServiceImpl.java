package dev.scaraz.mars.core.query.impl;

import dev.scaraz.mars.common.tools.enums.AgStatus;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.common.utils.QueryBuilder;
import dev.scaraz.mars.core.domain.order.TicketAgent;
import dev.scaraz.mars.core.query.TicketAgentQueryService;
import dev.scaraz.mars.core.query.TicketQueryService;
import dev.scaraz.mars.core.query.criteria.TicketAgentCriteria;
import dev.scaraz.mars.core.query.spec.TicketAgentSpecBuilder;
import dev.scaraz.mars.core.repository.order.TicketAgentRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor

@Service
@Transactional(readOnly = true)
public class TicketAgentQueryServiceImpl implements TicketAgentQueryService {

    private final TicketAgentRepo repo;
    private final TicketAgentSpecBuilder specBuilder;

    @Override
    public List<TicketAgent> findAll() {
        return repo.findAll();
    }

    @Override
    public Page<TicketAgent> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    public List<TicketAgent> findAll(TicketAgentCriteria criteria) {
        return repo.findAll(specBuilder.createSpec(criteria));
    }

    @Override
    public Page<TicketAgent> findAll(TicketAgentCriteria criteria, Pageable pageable) {
        return repo.findAll(specBuilder.createSpec(criteria), pageable);
    }

    @Override
    public long count() {
        return repo.count();
    }

    @Override
    public long count(TicketAgentCriteria criteria) {
        return repo.count(specBuilder.createSpec(criteria));
    }

    @Override
    public Optional<TicketAgent> findOne(TicketAgentCriteria criteria) {
        return repo.findOne(specBuilder.createSpec(criteria));
    }

    @Override
    public boolean hasAgentInProgressByTicketId(String id) {
        return repo.existsByTicketIdAndStatus(id, AgStatus.PROGRESS);
    }

    @Override
    public boolean hasAgentInProgressByTicketNo(String no) {
        return repo.existsByTicketNoAndStatus(no, AgStatus.PROGRESS);
    }

    @Override
    public List<TicketAgent> findByTicketId(String ticketId) {
        return findAll(TicketAgentCriteria.builder()
                .ticketId(new StringFilter().setEq(ticketId))
                .build());
    }

}

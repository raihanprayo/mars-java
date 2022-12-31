package dev.scaraz.mars.core.query.impl;

import dev.scaraz.mars.common.exception.web.NotFoundException;
import dev.scaraz.mars.common.utils.QueryBuilder;
import dev.scaraz.mars.core.domain.order.Ticket;
import dev.scaraz.mars.core.query.TicketQueryService;
import dev.scaraz.mars.core.query.criteria.TicketCriteria;
import dev.scaraz.mars.core.repository.order.TicketRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor

@Service
@Transactional(readOnly = true)
public class TicketQueryServiceImpl extends QueryBuilder implements TicketQueryService {

    private final TicketRepo repo;

    @Override
    public Ticket findById(String id) {
        return repo.findById(id)
                .orElseThrow(() -> NotFoundException.entity(Ticket.class, "id", id));
    }

    @Override
    public Ticket findByIdOrNo(String idOrNo) {
        return repo.findByIdOrNo(idOrNo, idOrNo)
                .orElseThrow(() -> NotFoundException.entity(Ticket.class, "id/no", idOrNo));
    }

    @Override
    public List<Ticket> findAll() {
        return repo.findAll();
    }

    @Override
    public Page<Ticket> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    public List<Ticket> findAll(TicketCriteria criteria) {
        return repo.findAll(createSpecification(criteria));
    }

    @Override
    public Page<Ticket> findAll(TicketCriteria criteria, Pageable pageable) {
        return repo.findAll(createSpecification(criteria), pageable);
    }

    @Override
    public int countByServiceNo(String serviceNo) {
        return repo.countByServiceNo(serviceNo);
    }

}

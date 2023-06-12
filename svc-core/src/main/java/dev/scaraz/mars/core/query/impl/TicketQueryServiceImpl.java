package dev.scaraz.mars.core.query.impl;

import dev.scaraz.mars.core.domain.db.ticket.Ticket;
import dev.scaraz.mars.core.query.TicketQueryService;
import dev.scaraz.mars.core.query.spec.TicketSpecBuilder;
import dev.scaraz.mars.core.repository.db.TicketRepo;
import dev.scaraz.mars.core.web.criteria.TicketCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor

@Service
@Transactional(readOnly = true)
public class TicketQueryServiceImpl implements TicketQueryService {

    private final TicketRepo repo;
    private final TicketSpecBuilder specBuilder;

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
        return repo.findAll(specBuilder.createSpec(criteria));
    }

    @Override
    public Page<Ticket> findAll(TicketCriteria criteria, Pageable pageable) {
        return repo.findAll(specBuilder.createSpec(criteria), pageable);
    }
}

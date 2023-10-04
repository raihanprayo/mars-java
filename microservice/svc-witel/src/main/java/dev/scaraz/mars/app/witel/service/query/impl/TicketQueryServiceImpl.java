package dev.scaraz.mars.app.witel.service.query.impl;

import dev.scaraz.mars.app.witel.domain.Issue;
import dev.scaraz.mars.app.witel.domain.order.Ticket;
import dev.scaraz.mars.app.witel.repository.TicketRepo;
import dev.scaraz.mars.app.witel.service.query.TicketQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TicketQueryServiceImpl implements TicketQueryService {

    private final TicketRepo repo;

    @Override
    public List<Ticket> findAll() {
        return repo.findAll();
    }

    @Override
    public Page<Ticket> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    public long count() {
        return repo.count();
    }

    public long countGoul(String service, Issue issue) {

    }

}

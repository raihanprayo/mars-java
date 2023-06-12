
package dev.scaraz.mars.core.service.impl;

import dev.scaraz.mars.core.domain.db.ticket.Ticket;
import dev.scaraz.mars.core.repository.db.TicketRepo;
import dev.scaraz.mars.core.service.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepo repo;

    @Override
    public Ticket save(Ticket t) {
        return repo.save(t);
    }

    @Override
    public List<Ticket> saveAll(Iterable<Ticket> ts) {
        return repo.saveAll(ts);
    }

}

package dev.scaraz.mars.core.v2.service.order.impl;

import dev.scaraz.mars.core.v2.domain.order.Ticket;
import dev.scaraz.mars.core.v2.repository.db.order.TicketHistoryRepo;
import dev.scaraz.mars.core.v2.repository.db.order.TicketRepo;
import dev.scaraz.mars.core.v2.service.order.TicketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor

@Service
public class TicketServiceImpl implements TicketService {
    private final TicketRepo repo;
    private final TicketHistoryRepo historyRepo;

    @Override
    public Ticket save(Ticket t) {
        return repo.save(t);
    }

    public void createTicketLog(Ticket ticket, String message) {

    }

}

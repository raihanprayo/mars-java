package dev.scaraz.mars.core.service;

import dev.scaraz.mars.core.domain.db.ticket.Ticket;

import java.util.List;

public interface TicketService {
    Ticket save(Ticket t);

    List<Ticket> saveAll(Iterable<Ticket> ts);
}

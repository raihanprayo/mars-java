package dev.scaraz.mars.app.witel.service.app;

import dev.scaraz.mars.app.witel.domain.order.Ticket;

public interface TicketService {
    Ticket save(Ticket ticket);

    String generateTicketNo();
}

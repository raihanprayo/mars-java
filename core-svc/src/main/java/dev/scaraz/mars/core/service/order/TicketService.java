package dev.scaraz.mars.core.service.order;

import dev.scaraz.mars.common.domain.request.TicketStatusFormDTO;
import dev.scaraz.mars.core.domain.order.Ticket;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface TicketService {
    Ticket save(Ticket ticket);

    String generateTicketNo();
}

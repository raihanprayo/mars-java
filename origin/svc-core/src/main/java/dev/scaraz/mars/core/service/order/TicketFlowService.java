package dev.scaraz.mars.core.service.order;

import dev.scaraz.mars.core.domain.order.Ticket;

public interface TicketFlowService {

    Ticket take(String ticketIdOrNo);

}

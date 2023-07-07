package dev.scaraz.mars.v1.core.service.order;

import dev.scaraz.mars.v1.core.domain.order.Ticket;

public interface TicketFlowService {

    Ticket take(String ticketIdOrNo);

}

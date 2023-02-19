package dev.scaraz.mars.core.service.order;

import dev.scaraz.mars.common.domain.request.TicketStatusFormDTO;
import dev.scaraz.mars.core.domain.order.Ticket;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

public interface TicketFlowService {

    Ticket take(String ticketIdOrNo);

}

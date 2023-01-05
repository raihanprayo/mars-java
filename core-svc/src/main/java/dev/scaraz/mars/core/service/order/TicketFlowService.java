package dev.scaraz.mars.core.service.order;

import dev.scaraz.mars.common.domain.request.TicketStatusFormDTO;
import dev.scaraz.mars.core.domain.order.Ticket;

public interface TicketFlowService {
    String LOG_AUTO_CLOSE = "auto close",
            LOG_CONFIRMED_CLOSE = "confirmed request, closing ticket",
            LOG_REOPEN = "reopen ticket",
            LOG_CLOSE_CONFIRMATION = "close confirmation request",
            LOG_DISPATCH_REQUEST = "ticket dispatched",
            LOG_WORK_IN_PROGRESS = "work in progress";

    Ticket take(String ticketIdOrNo);

    Ticket close(String ticketIdOrNo, TicketStatusFormDTO form);

    Ticket dispatch(String ticketIdOrNo, TicketStatusFormDTO form);

    Ticket confirm(String ticketIdOrNo, boolean reopen, TicketStatusFormDTO form);
}

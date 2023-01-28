package dev.scaraz.mars.core.service.order;

import dev.scaraz.mars.common.domain.request.TicketStatusFormDTO;
import dev.scaraz.mars.core.domain.order.Ticket;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;

public interface TicketFlowService {
    String LOG_AUTO_CLOSE = "auto close",
            LOG_AUTO_CLOSE_PENDING = "pending auto close",
            LOG_CONFIRMED_CLOSE = "request confirmed, closing ticket",
            LOG_CONFIRMED_PENDING = "request confirmed, pending ticket",
            LOG_REOPEN = "reopen ticket",
            LOG_CLOSE_CONFIRMATION = "close confirmation request",
            LOG_PENDING_CONFIRMATION = "pending confirmation request",
            LOG_DISPATCH_REQUEST = "ticket dispatched",
            LOG_WORK_IN_PROGRESS = "work in progress",
            LOG_REWORK_IN_PROGRESS = "rework in progress";

    Ticket take(String ticketIdOrNo);

    Ticket close(String ticketIdOrNo, TicketStatusFormDTO form);

    Ticket dispatch(String ticketIdOrNo, TicketStatusFormDTO form);

    Ticket confirmClose(String ticketIdOrNo, boolean reopen, TicketStatusFormDTO form);

    @Async
    void confirmCloseAsync(String ticketIdOrNo, boolean reopen, TicketStatusFormDTO form);

    Ticket confirmPending(String ticketIdOrNo, boolean doPending, TicketStatusFormDTO form);

    void confirmPendingAsync(String ticketIdOrNo, boolean doPending, TicketStatusFormDTO form);

    void askPostPending(String ticketNo);

    @Transactional
    Ticket confirmPostPending(String ticketNo, TicketStatusFormDTO form);
}

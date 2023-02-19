package dev.scaraz.mars.core.service.order;

import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.core.domain.order.LogTicket;
import dev.scaraz.mars.core.domain.order.Ticket;
import dev.scaraz.mars.core.domain.order.Agent;
import dev.scaraz.mars.core.repository.order.LogTicketRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor

@Service
public class LogTicketService {

    public static final String LOG_AUTO_CLOSE = "(auto) closed";
    public static final String LOG_CONFIRMED_CLOSE = "confirmed ticket closed";
    public static final String LOG_CONFIRMED_PENDING = "confirmed ticket pending";
    public static final String LOG_REOPEN = "ticket reopened";
    public static final String LOG_CLOSE_CONFIRMATION = "close confirmation request";
    public static final String LOG_PENDING_CONFIRMATION = "pending confirmation request";
    public static final String LOG_DISPATCH_REQUEST = "ticket dispatched";
    public static final String LOG_WORK_IN_PROGRESS = "work in progress";
    public static final String LOG_REWORK_IN_PROGRESS = "rework in progress";
    private final LogTicketRepo repo;

    public LogTicket add(LogTicket history) {
        log.debug("ADD UPDATE HISTORY -- TICKET NO {}", history.getTicket().getNo());
        return repo.save(history);
    }

}

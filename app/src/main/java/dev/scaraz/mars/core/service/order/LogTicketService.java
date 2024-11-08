package dev.scaraz.mars.core.service.order;

import dev.scaraz.mars.core.domain.order.LogTicket;
import dev.scaraz.mars.core.repository.db.order.LogTicketRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor

@Service
public class LogTicketService {

    public static final String LOG_AUTO_CLOSE = "(system) tiket ditutup";
    public static final String LOG_CONFIRMED_CLOSE = "telah dikonfirmasi untuk melakukan penutupan tiket";
    public static final String LOG_CONFIRMED_PENDING = "telah dikonfirmasi untuk melakukan pending tiket";
    public static final String LOG_REOPEN = "ticket dibuka kembali";
    public static final String LOG_FORCE_CLOSE = "tiket ditutup secara paksa";
    public static final String LOG_CLOSE_CONFIRMATION = "permintaan penutupan tiket";
    public static final String LOG_PENDING_CONFIRMATION = "permintaan melakukan pending tiket";
    public static final String LOG_DISPATCH_REQUEST = "ticket dispatched";
    public static final String LOG_WORK_IN_PROGRESS = "dalam pengerjaan";
    public static final String LOG_REWORK_IN_PROGRESS = "rework in progress";

    private final LogTicketRepo repo;

    public LogTicket add(LogTicket history) {
        log.debug("ADD UPDATE HISTORY -- TICKET NO {}", history.getTicket().getNo());
        return repo.save(history);
    }

    @Transactional(readOnly = true)
    public Optional<LogTicket> getLogByTicketIdAndBelow(String ticketId, Instant belowTimstamp) {
        return repo.findFirstByTicketIdAndCreatedAtLessThanOrderByCreatedAtDesc(ticketId, belowTimstamp);
    }

    @Transactional
    public void deleteAllByTicketNo(String id) {
        repo.deleteAllByTicketNo(id);
    }

}

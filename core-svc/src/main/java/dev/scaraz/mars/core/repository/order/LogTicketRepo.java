package dev.scaraz.mars.core.repository.order;

import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.core.domain.order.LogTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface LogTicketRepo extends JpaRepository<LogTicket, Long> {

    List<LogTicket> findAllByTicketIdIn(List<String> ticketIds);
    List<LogTicket> findAllByTicketIdInOrderByCreatedAtAsc(List<String> ticketIds);
    LogTicket findByTicketId(String ticketId);

    LogTicket findByTicketIdAndPrevAndCurr(String ticketId, TcStatus prev, TcStatus curr);

    List<LogTicket> findAllByCreatedAtGreaterThanEqualAndCreatedAtLessThanEqualOrderByCreatedAtAsc(
            Instant from,
            Instant to
    );

    List<LogTicket> findAllByPrevAndCurrAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual(
            TcStatus previous, TcStatus current,
            Instant from, Instant to);

    List<LogTicket> findAllByTicketIdOrTicketNo(String ticketId, String ticketNo);
}

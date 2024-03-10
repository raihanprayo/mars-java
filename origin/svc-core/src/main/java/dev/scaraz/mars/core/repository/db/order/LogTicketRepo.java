package dev.scaraz.mars.core.repository.db.order;

import dev.scaraz.mars.core.domain.order.LogTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface LogTicketRepo extends JpaRepository<LogTicket, Long> {

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "delete from t_log_ticket lt where lt.ref_ticket_no = :ticketNo")
    void deleteAllByTicketNo(String ticketNo);

    List<LogTicket> findAllByTicketIdOrTicketNo(String ticketId, String ticketNo);

    List<LogTicket> findAllByTicketIdOrTicketNoOrderByCreatedAtAsc(String ticketId, String ticketNo);

    Optional<LogTicket> findFirstByTicketIdAndCreatedAtLessThanOrderByCreatedAtDesc(String ticketId, Instant lessThanTimestamp);
}

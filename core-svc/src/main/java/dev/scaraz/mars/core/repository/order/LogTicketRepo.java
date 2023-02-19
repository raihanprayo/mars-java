package dev.scaraz.mars.core.repository.order;

import dev.scaraz.mars.core.domain.order.LogTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LogTicketRepo extends JpaRepository<LogTicket, Long> {

    List<LogTicket> findAllByTicketIdOrTicketNo(String ticketId, String ticketNo);
}

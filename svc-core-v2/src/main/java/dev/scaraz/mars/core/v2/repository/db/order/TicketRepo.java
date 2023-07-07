package dev.scaraz.mars.core.v2.repository.db.order;

import dev.scaraz.mars.core.v2.domain.order.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepo extends JpaRepository<Ticket, String> {
}

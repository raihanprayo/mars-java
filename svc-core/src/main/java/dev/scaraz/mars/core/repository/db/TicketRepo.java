package dev.scaraz.mars.core.repository.db;

import dev.scaraz.mars.core.domain.db.ticket.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TicketRepo extends
        JpaRepository<Ticket, String>,
        JpaSpecificationExecutor<Ticket> {
}

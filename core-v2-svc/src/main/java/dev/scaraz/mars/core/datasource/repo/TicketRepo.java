package dev.scaraz.mars.core.datasource.repo;

import dev.scaraz.mars.core.datasource.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketRepo extends JpaRepository<Ticket, String> {
}

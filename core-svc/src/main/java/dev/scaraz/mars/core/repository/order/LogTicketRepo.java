package dev.scaraz.mars.core.repository.order;

import dev.scaraz.mars.core.domain.order.LogTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogTicketRepo extends JpaRepository<LogTicket, Long> {
}

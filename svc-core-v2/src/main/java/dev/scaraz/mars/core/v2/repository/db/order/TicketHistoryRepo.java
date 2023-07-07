package dev.scaraz.mars.core.v2.repository.db.order;

import dev.scaraz.mars.core.v2.domain.order.TicketHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketHistoryRepo extends JpaRepository<TicketHistory, String> {
}

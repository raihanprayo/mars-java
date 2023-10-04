package dev.scaraz.mars.app.witel.repository;

import dev.scaraz.mars.app.witel.domain.order.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface TicketRepo extends JpaRepository<Ticket, String> {

    long countByCreatedAtGreaterThanEqual(Instant today);

    long countByServiceNoAndIssueIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual(
            String serviceNo,
            String issueId,
            Instant from,
            Instant to
    );

}

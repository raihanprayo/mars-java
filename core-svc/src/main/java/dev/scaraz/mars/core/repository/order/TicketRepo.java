package dev.scaraz.mars.core.repository.order;

import dev.scaraz.mars.core.domain.order.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface TicketRepo extends JpaRepository<Ticket, String>, JpaSpecificationExecutor<Ticket> {

    long countByCreatedAtAfter(Instant currentDate);

    long countByCreatedAtGreaterThanEqual(Instant today);

    int countByServiceNo(String serviceNo);

    Optional<Ticket> findByIdOrNo(String ticketId, String ticketNo);

}

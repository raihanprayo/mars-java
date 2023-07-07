package dev.scaraz.mars.core.repository.order;

import dev.scaraz.mars.common.tools.enums.AgStatus;
import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.core.domain.order.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.Instant;
import java.util.Collection;
import java.util.Optional;

@Repository
public interface TicketRepo extends JpaRepository<Ticket, String>, JpaSpecificationExecutor<Ticket> {

    long countByCreatedAtAfter(Instant currentDate);

    long countByCreatedAtGreaterThanEqual(Instant today);

    int countByServiceNoAndIssueIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual(
            String serviceNo,
            long issueId,
            Instant gte,
            Instant lte
    );

    Optional<Ticket> findByIdOrNo(String ticketId, String ticketNo);
    Optional<Ticket> findOneByConfirmMessageId(Long messageId);

}

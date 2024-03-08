package dev.scaraz.mars.core.repository.db.order;

import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.core.domain.order.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collection;
import java.util.Optional;

@Repository
public interface TicketRepo extends JpaRepository<Ticket, String>, JpaSpecificationExecutor<Ticket> {

    long countByCreatedAtGreaterThanEqual(Instant today);

    int countByServiceNoAndIssueIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual(
            String serviceNo,
            long issueId,
            Instant gte,
            Instant lte
    );

    Optional<Ticket> findByIdOrNo(String ticketId, String ticketNo);

    Optional<Ticket> findOneByConfirmMessageId(Long messageId);

    @Query("select sum(t.issue.score) from Ticket t where t.id in (:ids)")
    BigDecimal sumTotalScore(Collection<String> ids);

    @Modifying
    @Query("update Ticket as tc set " +
            "tc.issue.name = :issName, " +
            "tc.issue.product = :issProduct, " +
            "tc.issue.description = :issDesc, " +
            "tc.issue.score = :issScore " +
            "where tc.issue.id = :issueId")
    void updateIssueByIssueId(long issueId, String issName, String issDesc, Product issProduct, double issScore);

}

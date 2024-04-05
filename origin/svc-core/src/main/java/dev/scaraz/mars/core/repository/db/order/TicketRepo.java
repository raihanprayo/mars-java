package dev.scaraz.mars.core.repository.db.order;

import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.core.domain.order.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;

@Repository
public interface TicketRepo extends JpaRepository<Ticket, String>, JpaSpecificationExecutor<Ticket> {

    void deleteAllByCreatedAtLessThanEqual(Instant belowDate);

    long deleteAllByDeletedIsFalseAndStatusAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual(TcStatus status, Instant from, Instant to);

    @Modifying
    @Transactional
    @Query("update Ticket t set t.deleted = false, t.deletedAt = null where t.id = :id")
    void restoreById(String id);

    @Modifying
    @Transactional
    @Query("update Ticket t set t.deleted = false, t.deletedAt = null where t.id in (:ids)")
    void restoreByIds(String... ids);

//    @Modifying
//    @Transactional
//    @Query("delete from Ticket t where t.id = :id")
//    void deleteForGoodById(String id);
//
//    @Modifying
//    @Transactional
//    @Query("delete from Ticket t where t.id in (:ids)")
//    void deleteForGoodByIds(String... ids);

    long countByCreatedAtGreaterThanEqual(Instant today);

    int countByServiceNoAndIssueIdAndCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual(
            String serviceNo,
            long issueId,
            Instant gte,
            Instant lte
    );

    Optional<Ticket> findByIdOrNo(String ticketId, String ticketNo);

    Optional<Ticket> findOneByConfirmMessageId(Long messageId);

    @Query("select coalesce(sum(coalesce(t.issue.score, 0)), 0) from Ticket t where t.id in (:ids)")
    double sumTotalScore(Collection<String> ids);

    @Modifying
    @Query("update Ticket as tc set " +
            "tc.issue.name = :issName, " +
            "tc.issue.product = :issProduct, " +
            "tc.issue.description = :issDesc, " +
            "tc.issue.score = :issScore " +
            "where tc.issue.id = :issueId")
    void updateIssueByIssueId(long issueId, String issName, String issDesc, Product issProduct, double issScore);

}

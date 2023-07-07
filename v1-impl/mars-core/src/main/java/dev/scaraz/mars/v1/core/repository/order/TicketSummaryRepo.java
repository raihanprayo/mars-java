package dev.scaraz.mars.v1.core.repository.order;

import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.v1.core.domain.view.TicketSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketSummaryRepo extends
        JpaRepository<TicketSummary, String>,
        JpaSpecificationExecutor<TicketSummary> {

    @Query("select tc.id from TicketSummary tc " +
            "where tc.createdAt >= :from and tc.createdAt <= :to " +
            "order by tc.createdAt asc")
    List<String> getAllIds(Instant from, Instant to);

    Optional<TicketSummary> findByIdOrNo(String id, String no);

    long countByProductAndWipIsFalse(Product product);

    long countByProductAndWipBy(Product product, String userId);
}

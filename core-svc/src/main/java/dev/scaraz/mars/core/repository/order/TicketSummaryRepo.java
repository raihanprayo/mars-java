package dev.scaraz.mars.core.repository.order;

import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.core.domain.view.TicketSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketSummaryRepo extends
        JpaRepository<TicketSummary, String>,
        JpaSpecificationExecutor<TicketSummary> {

    Optional<TicketSummary> findByIdOrNo(String id, String no);

    long countByProductAndWipIsFalse(Product product);
    long countByProductAndWipById(Product product, String userId);
}

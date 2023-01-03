package dev.scaraz.mars.core.repository.order;

import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.core.domain.view.TicketSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketSummaryRepo extends
        JpaRepository<TicketSummary, String>,
        JpaSpecificationExecutor<TicketSummary> {

    long countByProduct(Product product);
    long countByProductAndWipById(Product product, String userId);
}

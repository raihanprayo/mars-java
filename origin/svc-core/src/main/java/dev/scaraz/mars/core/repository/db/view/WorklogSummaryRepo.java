package dev.scaraz.mars.core.repository.db.view;

import dev.scaraz.mars.core.domain.view.WorklogSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorklogSummaryRepo extends
        JpaRepository<WorklogSummary, Long>,
        JpaSpecificationExecutor<WorklogSummary> {

    List<WorklogSummary> findAllByTicketIdOrderByWlCreatedAtDesc(String ticketId);

    Optional<WorklogSummary> findFirstByTicketIdAndWlCreatedAtLessThan(String ticketId, Instant lessThanWlCreatedAt);

}

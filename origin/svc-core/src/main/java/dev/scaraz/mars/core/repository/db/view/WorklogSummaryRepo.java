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

    List<WorklogSummary> findAllByTicketIdOrderByCreatedAtDesc(String ticketId);

    Optional<WorklogSummary> findFirstByTicketIdAndCreatedAtLessThan(String ticketId, Instant lessThanWlCreatedAt);

}

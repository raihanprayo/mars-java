package dev.scaraz.mars.core.repository.db.agent;

import dev.scaraz.mars.core.domain.agent.Leaderboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface WorkSummaryRepo extends JpaRepository<Leaderboard, Long>, JpaSpecificationExecutor<Leaderboard> {

    List<Leaderboard> findAllByAgIdAndSolutionIdNotInOrderById(String agentId, List<Long> notInSolId);
    List<Leaderboard> findAllByAgIdAndSolutionIdNotInAndTcCreatedAtBetweenOrderById(String agentId, List<Long> notInSolId, Instant start, Instant end);
    List<Leaderboard> findAllByAgIdAndSolutionIdNotInAndTcCreatedAtLessThanEqualOrderById(String agentId, List<Long> notInSolId, Instant end);
    List<Leaderboard> findAllByAgIdAndSolutionIdNotInAndTcCreatedAtGreaterThanEqualOrderById(String agentId, List<Long> notInSolId, Instant start);

    List<Leaderboard> findAllByAgIdAndTicketIdOrderById(String agentId, String ticketId);

}

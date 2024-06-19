package dev.scaraz.mars.core.query.spec;

import dev.scaraz.mars.common.utils.QueryBuilder;
import dev.scaraz.mars.core.domain.agent.Leaderboard;
import dev.scaraz.mars.core.domain.agent.WorkSummary_;
import dev.scaraz.mars.core.query.criteria.LeaderboardCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class LeaderboardSpecBuilder extends QueryBuilder<Leaderboard, LeaderboardCriteria> {


    @Override
    public Specification<Leaderboard> createSpec(LeaderboardCriteria criteria) {
        return chain()
                .pick(WorkSummary_.id, criteria.getId())
                .pick(WorkSummary_.ticketId, criteria.getTicketId())
                .pick(WorkSummary_.solutionId, criteria.getSolutionId())
                .pick(WorkSummary_.issueId, criteria.getIssueId())
                .pick(WorkSummary_.takeStatus, criteria.getTakeStatus())
                .pick(WorkSummary_.closeStatus, criteria.getCloseStatus())
                .pick(WorkSummary_.agId, criteria.getAgId())
                .pick(WorkSummary_.rqId, criteria.getRqId())
                .pick(WorkSummary_.tcCreatedAt, criteria.getTcCreatedAt())
                .specification();
    }
}

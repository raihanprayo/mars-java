package dev.scaraz.mars.core.query.spec;

import dev.scaraz.mars.common.utils.QueryBuilder;
import dev.scaraz.mars.core.domain.agent.Leaderboard;
import dev.scaraz.mars.core.domain.agent.Leaderboard_;
import dev.scaraz.mars.core.query.criteria.LeaderboardCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class LeaderboardSpecBuilder extends QueryBuilder<Leaderboard, LeaderboardCriteria> {


    @Override
    public Specification<Leaderboard> createSpec(LeaderboardCriteria criteria) {
        return chain()
                .pick(Leaderboard_.id, criteria.getId())
                .pick(Leaderboard_.ticketId, criteria.getTicketId())
                .pick(Leaderboard_.solutionId, criteria.getSolutionId())
                .pick(Leaderboard_.issueId, criteria.getIssueId())
                .pick(Leaderboard_.takeStatus, criteria.getTakeStatus())
                .pick(Leaderboard_.closeStatus, criteria.getCloseStatus())
                .pick(Leaderboard_.agId, criteria.getAgId())
                .pick(Leaderboard_.rqId, criteria.getRqId())
                .pick(Leaderboard_.tcCreatedAt, criteria.getTcCreatedAt())
                .specification();
    }
}

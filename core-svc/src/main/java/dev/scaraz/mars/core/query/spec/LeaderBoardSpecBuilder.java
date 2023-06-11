package dev.scaraz.mars.core.query.spec;

import dev.scaraz.mars.common.utils.QueryBuilder;
import dev.scaraz.mars.core.domain.view.LeaderBoardFragment;
import dev.scaraz.mars.core.domain.view.LeaderBoardFragment_;
import dev.scaraz.mars.core.query.criteria.LeaderBoardCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class LeaderBoardSpecBuilder extends QueryBuilder<LeaderBoardFragment, LeaderBoardCriteria> {
    @Override
    public Specification<LeaderBoardFragment> createSpec(LeaderBoardCriteria criteria) {
        return chain()
                .pick(LeaderBoardFragment_.ticketNo, criteria.getNo())
                .pick(LeaderBoardFragment_.issueName, criteria.getIssue())
                .pick(LeaderBoardFragment_.issueProduct, criteria.getProduct())
                .pick(LeaderBoardFragment_.ticketCreatedAt, criteria.getCreatedAt())
                .pick(LeaderBoardFragment_.ticketUpdatedAt, criteria.getUpdatedAt())
                .specification();
    }
}

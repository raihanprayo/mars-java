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
        Specification<LeaderBoardFragment> spec = Specification.where(null);
        if (criteria != null) {
            spec = nonNull(spec, criteria.getUserId(), LeaderBoardFragment_.userId);

            spec = nonNull(spec, criteria.getNo(), LeaderBoardFragment_.ticketNo);
            spec = nonNull(spec, criteria.getIssue(), LeaderBoardFragment_.issueName);
            spec = nonNull(spec, criteria.getProduct(), LeaderBoardFragment_.issueProduct);
            spec = nonNull(spec, criteria.getCreatedAt(), LeaderBoardFragment_.ticketCreatedAt);
            spec = nonNull(spec, criteria.getUpdatedAt(), LeaderBoardFragment_.ticketUpdatedAt);
        }
        return spec;
    }
}

package dev.scaraz.mars.v1.core.query.spec;

import dev.scaraz.mars.common.utils.QueryBuilder;
import dev.scaraz.mars.core.domain.order.Agent_;
import dev.scaraz.mars.core.domain.order.Ticket_;
import dev.scaraz.mars.v1.core.domain.view.LeaderBoardFragment;
import dev.scaraz.mars.core.domain.view.LeaderBoardFragment_;
import dev.scaraz.mars.v1.core.query.criteria.LeaderBoardCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class LeaderBoardSpecBuilder extends QueryBuilder<LeaderBoardFragment, LeaderBoardCriteria> {
    @Override
    public Specification<LeaderBoardFragment> createSpec(LeaderBoardCriteria criteria) {
        SpecChain<LeaderBoardFragment> chainer = chain()
                .pick(criteria.getTicketId(), r -> r.get(LeaderBoardFragment_.ticket).get(Ticket_.id))
                .pick(criteria.getTicketNo(), r -> r.get(LeaderBoardFragment_.ticket).get(Ticket_.no))
                .pick(criteria.getUserId(), r -> r.get(LeaderBoardFragment_.agent).get(Agent_.userId))
                .pick(LeaderBoardFragment_.issue, criteria.getIssue())
                .pick(LeaderBoardFragment_.product, criteria.getProduct())
                .pick(LeaderBoardFragment_.ticketCreatedAt, criteria.getCreatedAt())
                .pick(LeaderBoardFragment_.ticketUpdatedAt, criteria.getUpdatedAt());

        return chainer.specification();
    }
}

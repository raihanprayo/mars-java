package dev.scaraz.mars.core.query.spec;

import dev.scaraz.mars.common.utils.QueryBuilder;
import dev.scaraz.mars.core.domain.order.Agent_;
import dev.scaraz.mars.core.domain.order.TcIssue_;
import dev.scaraz.mars.core.domain.order.Ticket_;
import dev.scaraz.mars.core.domain.order.WlSolution_;
import dev.scaraz.mars.core.domain.view.LeaderBoardFragment;
import dev.scaraz.mars.core.domain.view.LeaderBoardFragment_;
import dev.scaraz.mars.core.query.criteria.IssueCriteria;
import dev.scaraz.mars.core.query.criteria.LeaderBoardCriteria;
import dev.scaraz.mars.core.query.criteria.SolutionCriteria;
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
                .pick(LeaderBoardFragment_.ticketCreatedAt, criteria.getCreatedAt())
                .pick(LeaderBoardFragment_.ticketUpdatedAt, criteria.getUpdatedAt());

        if (criteria.getSolution() != null) {
            SolutionCriteria solution = criteria.getSolution();
            chainer.pick(solution.getId(), path(LeaderBoardFragment_.solution, WlSolution_.id));
            chainer.pick(solution.getName(), path(LeaderBoardFragment_.solution, WlSolution_.name));
        }

        if (criteria.getIssue() != null) {
            IssueCriteria issue = criteria.getIssue();
            chainer.pick(issue.getId(), path(LeaderBoardFragment_.issue, TcIssue_.id));
            chainer.pick(issue.getName(), path(LeaderBoardFragment_.issue, TcIssue_.name));
            chainer.pick(issue.getProduct(), path(LeaderBoardFragment_.issue, TcIssue_.product));
        }

        return chainer.specification();
    }
}

package dev.scaraz.mars.core.query.spec;

import dev.scaraz.mars.common.query.AuditableSpec;
import dev.scaraz.mars.core.domain.order.TcIssue_;
import dev.scaraz.mars.core.domain.order.WlSolution_;
import dev.scaraz.mars.core.domain.view.LeaderBoardFragment;
import dev.scaraz.mars.core.domain.view.LeaderBoardFragment_;
import dev.scaraz.mars.core.query.criteria.IssueCriteria;
import dev.scaraz.mars.core.query.criteria.LeaderBoardCriteria;
import dev.scaraz.mars.core.query.criteria.SolutionCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class LeaderBoardSpecBuilder extends AuditableSpec<LeaderBoardFragment, LeaderBoardCriteria> {
    @Override
    public Specification<LeaderBoardFragment> createSpec(LeaderBoardCriteria criteria) {
        SpecChain<LeaderBoardFragment> chainer = chain()
                .pick(LeaderBoardFragment_.ticketId, criteria.getTicketId())
                .pick(LeaderBoardFragment_.workspaceId, criteria.getWorkspaceId())
                .pick(LeaderBoardFragment_.userId, criteria.getUserId())
                .pick(LeaderBoardFragment_.lastTicketLogAt, criteria.getLastTicketLogAt())
                .extend(s -> auditSpec(s, criteria));

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

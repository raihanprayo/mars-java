package dev.scaraz.mars.core.query.spec;

import dev.scaraz.mars.common.utils.QueryBuilder;
import dev.scaraz.mars.core.domain.order.TcIssue_;
import dev.scaraz.mars.core.domain.order.Ticket_;
import dev.scaraz.mars.core.domain.order.WlSolution_;
import dev.scaraz.mars.core.domain.view.WorklogSummary;
import dev.scaraz.mars.core.domain.view.WorklogSummary_;
import dev.scaraz.mars.core.query.criteria.SolutionCriteria;
import dev.scaraz.mars.core.query.criteria.TicketCriteria;
import dev.scaraz.mars.core.query.criteria.WorklogSummaryCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class WorklogSummarySpecBuilder extends QueryBuilder<WorklogSummary, WorklogSummaryCriteria> {
    @Override
    public Specification<WorklogSummary> createSpec(WorklogSummaryCriteria criteria) {
        SpecChain<WorklogSummary> chainer = chain()
                .pick(WorklogSummary_.id, criteria.getId())
                .pick(WorklogSummary_.workspaceId, criteria.getWorkspaceId())
                .pick(WorklogSummary_.userId, criteria.getUserId())
                .pick(WorklogSummary_.status, criteria.getStatus())
                .pick(WorklogSummary_.takeStatus, criteria.getTakeStatus())
                .pick(WorklogSummary_.closeStatus, criteria.getCloseStatus())
                .pick(WorklogSummary_.wsCreatedAt, criteria.getWsCreatedAt())
                .pick(WorklogSummary_.wsUpdatedAt, criteria.getWsUpdatedAt())
                .pick(WorklogSummary_.createdBy, criteria.getCreatedBy())
                .pick(WorklogSummary_.createdAt, criteria.getCreatedAt())
                .pick(WorklogSummary_.updatedBy, criteria.getUpdatedBy())
                .pick(WorklogSummary_.updatedAt, criteria.getUpdatedAt());

        if (criteria.getSolution() != null) {
            SolutionCriteria solution = criteria.getSolution();
            chainer.pick(solution.getId(), path(WorklogSummary_.solution, WlSolution_.id));
            chainer.pick(solution.getName(), path(WorklogSummary_.solution, WlSolution_.name));
        }

        if (criteria.getTicket() != null) {
            TicketCriteria ticket = criteria.getTicket();
            chainer.pick(ticket.getId(), path(WorklogSummary_.ticket, Ticket_.id));
            chainer.pick(ticket.getNo(), path(WorklogSummary_.ticket, Ticket_.no));
            chainer.pick(ticket.getProduct(), path(WorklogSummary_.ticket, Ticket_.issue, TcIssue_.product));
        }

        return chainer.specification();
    }
}

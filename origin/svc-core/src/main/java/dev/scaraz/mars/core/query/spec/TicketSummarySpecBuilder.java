package dev.scaraz.mars.core.query.spec;

import dev.scaraz.mars.common.query.AuditableSpec;
import dev.scaraz.mars.core.domain.order.TcIssue_;
import dev.scaraz.mars.core.domain.view.TicketSummary;
import dev.scaraz.mars.core.domain.view.TicketSummary_;
import dev.scaraz.mars.core.query.criteria.IssueCriteria;
import dev.scaraz.mars.core.query.criteria.TicketSummaryCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TicketSummarySpecBuilder extends AuditableSpec<TicketSummary, TicketSummaryCriteria> {
    @Override
    public Specification<TicketSummary> createSpec(TicketSummaryCriteria criteria) {
        SpecChain<TicketSummary> chain = chain();

        if (criteria != null) {
            chain.pick(TicketSummary_.id, criteria.getId())
                    .pick(TicketSummary_.no, criteria.getNo())
                    .pick(TicketSummary_.witel, criteria.getWitel())
                    .pick(TicketSummary_.sto, criteria.getSto())
                    .pick(TicketSummary_.incidentNo, criteria.getIncidentNo())
                    .pick(TicketSummary_.serviceNo, criteria.getServiceNo())
                    .pick(TicketSummary_.status, criteria.getStatus())
                    .pick(TicketSummary_.source, criteria.getSource())
                    .pick(TicketSummary_.gaul, criteria.getGaul())
                    .pick(TicketSummary_.gaulCount, criteria.getGaulCount())
                    .pick(TicketSummary_.senderId, criteria.getSenderId())
                    .pick(TicketSummary_.senderName, criteria.getSenderName())
                    .pick(criteria.getProduct(), path(TicketSummary_.issue, TcIssue_.product))
                    .pick(TicketSummary_.wip, criteria.getWip())
                    .pick(TicketSummary_.wipBy, criteria.getWipBy())
                    .pick(TicketSummary_.closedAt, criteria.getClosedAt())
                    .pick(TicketSummary_.deleted, criteria.getDeleted())
                    .pick(TicketSummary_.deletedAt, criteria.getDeletedAt())
                    .extend(s -> auditSpec(s, criteria));

            if (criteria.getIssue() != null) {
                IssueCriteria issue = criteria.getIssue();
                chain.pick(issue.getId(), r -> r.get(TicketSummary_.issue).get(TcIssue_.id));
            }

        }

        return chain.specification();
    }
}

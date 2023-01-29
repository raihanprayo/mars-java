package dev.scaraz.mars.core.query.spec;

import dev.scaraz.mars.common.query.AuditableSpec;
import dev.scaraz.mars.core.domain.credential.UserTg_;
import dev.scaraz.mars.core.domain.credential.User_;
import dev.scaraz.mars.core.domain.order.Issue_;
import dev.scaraz.mars.core.domain.view.TicketSummary;
import dev.scaraz.mars.core.domain.view.TicketSummary_;
import dev.scaraz.mars.core.query.criteria.IssueCriteria;
import dev.scaraz.mars.core.query.criteria.TicketSummaryCriteria;
import dev.scaraz.mars.core.query.criteria.UserCriteria;
import dev.scaraz.mars.core.query.criteria.UserTgCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TicketSummarySpecBuilder extends AuditableSpec<TicketSummary, TicketSummaryCriteria> {
    @Override
    public Specification<TicketSummary> createSpec(TicketSummaryCriteria criteria) {
        Specification<TicketSummary> spec = Specification.where(null);

        if (criteria != null) {
            spec = nonNull(spec, criteria.getId(), TicketSummary_.id);
            spec = nonNull(spec, criteria.getNo(), TicketSummary_.no);
            spec = nonNull(spec, criteria.getWitel(), TicketSummary_.witel);
            spec = nonNull(spec, criteria.getSto(), TicketSummary_.sto);
            spec = nonNull(spec, criteria.getIncidentNo(), TicketSummary_.incidentNo);
            spec = nonNull(spec, criteria.getServiceNo(), TicketSummary_.serviceNo);

            spec = nonNull(spec, criteria.getStatus(), TicketSummary_.status);
            spec = nonNull(spec, criteria.getSource(), TicketSummary_.source);
            spec = nonNull(spec, criteria.getGaul(), TicketSummary_.gaul);
            spec = nonNull(spec, criteria.getGaulCount(), TicketSummary_.gaulCount);

            spec = nonNull(spec, criteria.getSenderId(), TicketSummary_.senderId);
            spec = nonNull(spec, criteria.getSenderName(), TicketSummary_.senderName);

            spec = nonNull(spec, criteria.getProduct(), TicketSummary_.product);

            spec = nonNull(spec, criteria.getWip(), TicketSummary_.wip);

            if (criteria.getWipBy() != null) {
                UserCriteria wip = criteria.getWipBy();
                spec = nonNull(spec, wip.getId(), TicketSummary_.wipBy, User_.id);
                spec = nonNull(spec, wip.getName(), TicketSummary_.wipBy, User_.name);
                spec = nonNull(spec, wip.getNik(), TicketSummary_.wipBy, User_.nik);

                if (wip.getTg() != null) {
                    UserTgCriteria tg = wip.getTg();
                    spec = nonNull(spec, tg.getId(), TicketSummary_.wipBy, User_.tg, UserTg_.id);
                    spec = nonNull(spec, tg.getUsername(), TicketSummary_.wipBy, User_.tg, UserTg_.username);
                }
            }
            if (criteria.getIssue() != null) {
                IssueCriteria issue = criteria.getIssue();
                spec = nonNull(spec, issue.getId(), TicketSummary_.issue, Issue_.id);
            }
        }

        return auditSpec(spec, criteria);
    }
}

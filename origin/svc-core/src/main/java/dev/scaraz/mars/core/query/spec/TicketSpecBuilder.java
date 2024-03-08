package dev.scaraz.mars.core.query.spec;

import dev.scaraz.mars.common.query.AuditableSpec;
import dev.scaraz.mars.core.domain.order.Issue_;
import dev.scaraz.mars.core.domain.order.Ticket;
import dev.scaraz.mars.core.domain.order.Ticket_;
import dev.scaraz.mars.core.query.criteria.IssueCriteria;
import dev.scaraz.mars.core.query.criteria.TicketCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import jakarta.persistence.metamodel.SingularAttribute;

@Component
public class TicketSpecBuilder extends AuditableSpec<Ticket, TicketCriteria> {
    @Override
    public Specification<Ticket> createSpec(TicketCriteria criteria) {
        SpecChain<Ticket> chain = chain();

        if (criteria != null) {
            chain.pick(Ticket_.id, criteria.getId())
                    .pick(Ticket_.no, criteria.getNo())
                    .pick(Ticket_.witel, criteria.getWitel())
                    .pick(Ticket_.sto, criteria.getSto())
                    .pick(Ticket_.incidentNo, criteria.getIncidentNo())
                    .pick(Ticket_.serviceNo, criteria.getServiceNo())
                    .pick(Ticket_.status, criteria.getStatus())
                    .pick(Ticket_.source, criteria.getSource())
                    .pick(Ticket_.gaul, criteria.getGaul())
                    .pick(Ticket_.senderId, criteria.getSenderId())
                    .pick(Ticket_.senderName, criteria.getSenderName())
                    .pick(criteria.getProduct(), r -> r.get(Ticket_.issue).get(Issue_.product))
                    .extend(s -> auditSpec(s, criteria));

            if (criteria.getIssue() != null) {
                IssueCriteria issue = criteria.getIssue();
                chain.pick(issue.getId(), r -> r.get(Ticket_.issue).get(Issue_.id))
                        .pick(issue.getName(), r -> r.get(Ticket_.issue).get(Issue_.name))
                        .pick(issue.getProduct(), r -> r.get(Ticket_.issue).get(Issue_.product))
                        .extend(s -> auditSpec(s, Ticket_.issue, issue));
            }
        }
        return chain.specification();
    }

    public <T> Specification<T> createSpec(Specification<T> spec,
                                           SingularAttribute<T, Ticket> junction,
                                           TicketCriteria criteria
    ) {
        SpecSingleChain<Ticket, T> chain = chain(spec, junction);
        if (criteria != null) {
            chain.pick(Ticket_.id, criteria.getId())
                    .pick(Ticket_.no, criteria.getNo())
                    .pick(Ticket_.witel, criteria.getWitel())
                    .pick(Ticket_.sto, criteria.getSto())
                    .pick(Ticket_.incidentNo, criteria.getIncidentNo())
                    .pick(Ticket_.serviceNo, criteria.getServiceNo())
                    .pick(Ticket_.status, criteria.getStatus())
                    .pick(Ticket_.source, criteria.getSource())
                    .pick(Ticket_.gaul, criteria.getGaul())
                    .pick(Ticket_.senderId, criteria.getSenderId())
                    .pick(Ticket_.senderName, criteria.getSenderName())
                    .pick(criteria.getProduct(), r -> r.get(Ticket_.issue).get(Issue_.product));

            if (criteria.getIssue() != null) {
                IssueCriteria issue = criteria.getIssue();
                chain.pick(issue.getId(), r -> r.get(Ticket_.issue).get(Issue_.id))
                        .pick(issue.getName(), r -> r.get(Ticket_.issue).get(Issue_.name))
                        .pick(issue.getProduct(), r -> r.get(Ticket_.issue).get(Issue_.product));
            }
        }
        return chain.specification();
    }
}

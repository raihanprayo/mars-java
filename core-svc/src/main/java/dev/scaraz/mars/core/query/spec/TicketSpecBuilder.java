package dev.scaraz.mars.core.query.spec;

import dev.scaraz.mars.common.query.AuditableSpec;
import dev.scaraz.mars.core.domain.order.Issue_;
import dev.scaraz.mars.core.domain.order.Ticket;
import dev.scaraz.mars.core.domain.order.Ticket_;
import dev.scaraz.mars.core.query.criteria.IssueCriteria;
import dev.scaraz.mars.core.query.criteria.TicketCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.metamodel.SingularAttribute;

@Component
public class TicketSpecBuilder extends AuditableSpec<Ticket, TicketCriteria> {
    @Override
    public Specification<Ticket> createSpec(TicketCriteria criteria) {
        Specification<Ticket> spec = Specification.where(null);
        if (criteria != null) {
            spec = nonNull(spec, criteria.getId(), Ticket_.id);
            spec = nonNull(spec, criteria.getNo(), Ticket_.no);
            spec = nonNull(spec, criteria.getWitel(), Ticket_.witel);
            spec = nonNull(spec, criteria.getSto(), Ticket_.sto);
            spec = nonNull(spec, criteria.getIncidentNo(), Ticket_.incidentNo);
            spec = nonNull(spec, criteria.getServiceNo(), Ticket_.serviceNo);

            spec = nonNull(spec, criteria.getStatus(), Ticket_.status);
            spec = nonNull(spec, criteria.getSource(), Ticket_.source);
            spec = nonNull(spec, criteria.getGaul(), Ticket_.gaul);

            spec = nonNull(spec, criteria.getSenderId(), Ticket_.senderId);
            spec = nonNull(spec, criteria.getSenderName(), Ticket_.senderName);

            spec = nonNull(spec, criteria.getProduct(), Ticket_.issue, Issue_.product);

            if (criteria.getIssue() != null) {
                IssueCriteria issue = criteria.getIssue();
                spec = nonNull(spec, issue.getId(), Ticket_.issue, Issue_.id);
                spec = nonNull(spec, issue.getName(), Ticket_.issue, Issue_.name);
                spec = nonNull(spec, issue.getProduct(), Ticket_.issue, Issue_.product);
                spec = auditSpec(spec, Ticket_.issue, issue);
            }
//            if (criteria.getAgents() != null) {
//                AgentCriteria agents = criteria.getAgents();
//                spec = nonNull(spec, agents.getId(), Ticket_.agents, Agent_.id);
//                spec = nonNull(spec, agents.getStatus(), Ticket_.agents, Agent_.status);
//
//                spec = nonNull(spec, agents.getUserId(), Ticket_.agents, Agent_.user, User_.id);
//                spec = nonNull(spec, agents.getTicketId(), Ticket_.agents, Agent_.ticket, Ticket_.id);
//                spec = auditSpec(spec, Ticket_.issue, agents);
//            }
        }
        return auditSpec(spec, criteria);
    }
}

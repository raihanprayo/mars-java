package dev.scaraz.mars.core.query.spec;

import dev.scaraz.mars.common.query.AuditableSpec;
import dev.scaraz.mars.core.domain.order.Agent;
import dev.scaraz.mars.core.query.criteria.AgentCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class AgentSpecBuilder extends AuditableSpec<Agent, AgentCriteria> {
    @Override
    public Specification<Agent> createSpec(AgentCriteria criteria) {
        Specification<Agent> spec = Specification.where(null);
//        if (criteria != null) {
//            spec = nonNull(spec, criteria.getId(), Agent_.id);
//
//            spec = nonNull(spec, criteria.getTicketId(), Agent_.ticket, Ticket_.id);
//            spec = nonNull(spec, criteria.getTicketNo(), Agent_.ticket, Ticket_.no);
//            spec = nonNull(spec, criteria.getTakeStatus(), Agent_.takeStatus);
//            spec = nonNull(spec, criteria.getCloseStatus(), Agent_.closeStatus);
//            spec = nonNull(spec, criteria.getTicketNo(), Agent_.ticket, Ticket_.no);
//            spec = nonNull(spec, criteria.getUserId(), Agent_.user, User_.id);
//
//            if (criteria.getUser() != null) {
//                UserCriteria user = criteria.getUser();
//                spec = nonNull(spec, user.getId(), Agent_.user, User_.id);
//                spec = nonNull(spec, user.getName(), Agent_.user, User_.name);
//                spec = nonNull(spec, user.getNik(), Agent_.user, User_.nik);
//
//                if (user.getTg() != null) {
//                    UserTgCriteria tg = user.getTg();
//                    spec = nonNull(spec, tg.getId(), Agent_.user, User_.tg, UserTg_.id);
//                    spec = nonNull(spec, tg.getUsername(), Agent_.user, User_.tg, UserTg_.username);
//                }
//            }
//
//            if (criteria.getTicket() != null) {
//                TicketCriteria ticket = criteria.getTicket();
//                spec = nonNull(spec, ticket.getId(), Agent_.ticket, Ticket_.id);
//                spec = nonNull(spec, ticket.getNo(), Agent_.ticket, Ticket_.no);
//                spec = nonNull(spec, ticket.getStatus(), Agent_.ticket, Ticket_.status);
//                spec = nonNull(spec, ticket.getServiceNo(), Agent_.ticket, Ticket_.serviceNo);
//                spec = nonNull(spec, ticket.getIncidentNo(), Agent_.ticket, Ticket_.incidentNo);
//
//                spec = nonNull(spec, ticket.getProduct(), Agent_.ticket, Ticket_.issue, Issue_.product);
//                spec = auditSpec(spec, Agent_.ticket, ticket);
//            }
//        }
        return auditSpec(spec, criteria);
    }
}

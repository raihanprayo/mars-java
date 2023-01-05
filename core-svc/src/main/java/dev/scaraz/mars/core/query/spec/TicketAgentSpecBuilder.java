package dev.scaraz.mars.core.query.spec;

import dev.scaraz.mars.common.query.AuditableSpec;
import dev.scaraz.mars.core.domain.credential.User_;
import dev.scaraz.mars.core.domain.order.Issue_;
import dev.scaraz.mars.core.domain.order.TicketAgent;
import dev.scaraz.mars.core.domain.order.TicketAgent_;
import dev.scaraz.mars.core.domain.order.Ticket_;
import dev.scaraz.mars.core.query.criteria.TicketAgentCriteria;
import dev.scaraz.mars.core.query.criteria.TicketCriteria;
import dev.scaraz.mars.core.query.criteria.UserCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TicketAgentSpecBuilder extends AuditableSpec<TicketAgent, TicketAgentCriteria> {
    @Override
    public Specification<TicketAgent> createSpec(TicketAgentCriteria criteria) {
        Specification<TicketAgent> spec = Specification.where(null);
        if (criteria != null) {
            spec = nonNull(spec, criteria.getId(), TicketAgent_.id);

            spec = nonNull(spec, criteria.getTicketId(), TicketAgent_.ticket, Ticket_.id);
            spec = nonNull(spec, criteria.getTicketNo(), TicketAgent_.ticket, Ticket_.no);
            spec = nonNull(spec, criteria.getUserId(), TicketAgent_.user, User_.id);

            if (criteria.getUser() != null) {
                UserCriteria user = criteria.getUser();
                spec = nonNull(spec, user.getId(), TicketAgent_.user, User_.id);
                spec = nonNull(spec, user.getName(), TicketAgent_.user, User_.name);
                spec = nonNull(spec, user.getNik(), TicketAgent_.user, User_.nik);
                spec = nonNull(spec, user.getTelegramId(), TicketAgent_.user, User_.telegramId);
            }

            if (criteria.getTicket() != null) {
                TicketCriteria ticket = criteria.getTicket();
                spec = nonNull(spec, ticket.getId(), TicketAgent_.ticket, Ticket_.id);
                spec = nonNull(spec, ticket.getNo(), TicketAgent_.ticket, Ticket_.no);
                spec = nonNull(spec, ticket.getStatus(), TicketAgent_.ticket, Ticket_.status);
                spec = nonNull(spec, ticket.getServiceNo(), TicketAgent_.ticket, Ticket_.serviceNo);
                spec = nonNull(spec, ticket.getIncidentNo(), TicketAgent_.ticket, Ticket_.incidentNo);

                spec = nonNull(spec, ticket.getProduct(), TicketAgent_.ticket, Ticket_.issue, Issue_.product);
            }
        }
        return auditSpec(spec, criteria);
    }
}

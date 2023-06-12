package dev.scaraz.mars.core.query.spec;

import dev.scaraz.mars.common.query.AuditableSpec;
import dev.scaraz.mars.core.domain.db.ticket.Ticket;
import dev.scaraz.mars.core.domain.db.ticket.TicketSource;
import dev.scaraz.mars.core.domain.db.ticket.TicketSource_;
import dev.scaraz.mars.core.domain.db.ticket.Ticket_;
import dev.scaraz.mars.core.web.criteria.TicketCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class TicketSpecBuilder extends AuditableSpec<Ticket, TicketCriteria> {


    @Override
    public Specification<Ticket> createSpec(TicketCriteria criteria) {
        return chain()
                .pick(Ticket_.no, criteria.getNo())
                .pick(Ticket_.incidentNo, criteria.getIncidentNo())
                .pick(Ticket_.serviceNo, criteria.getServiceNo())
                .pick(Ticket_.status, criteria.getStatus())
                .pick(Ticket_.gaul, criteria.getGaul())
                .pick(Ticket_.product, criteria.getProduct())
                .pick(Ticket_.issue, criteria.getIssue())
                .pick(Ticket_.closedAt, criteria.getClosedAt())
                .pick(criteria.getWitel(), path(Ticket_.source, TicketSource_.witel))
                .pick(criteria.getSto(), path(Ticket_.source, TicketSource_.sto))
                .pick(criteria.getSenderName(), path(Ticket_.source, TicketSource_.senderName))
                .pick(criteria.getSenderTgId(), path(Ticket_.source, TicketSource_.senderTgId))
                .pick(criteria.getSource(), path(Ticket_.source, TicketSource_.source))
                .extend(s -> auditSpec(s, criteria))
                .specification();
    }
}

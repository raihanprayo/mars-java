package dev.scaraz.mars.core.query.spec;

import dev.scaraz.mars.common.query.TimestampSpec;
import dev.scaraz.mars.core.domain.order.*;
import dev.scaraz.mars.core.query.criteria.AgentCriteria;
import dev.scaraz.mars.core.query.criteria.AgentWorklogCriteria;
import dev.scaraz.mars.core.query.criteria.AgentWorkspaceCriteria;
import dev.scaraz.mars.core.query.criteria.TicketCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor

@Component
public class AgentWorklogSpecBuilder extends TimestampSpec<AgentWorklog, AgentWorklogCriteria> {

    private final TicketSpecBuilder ticketSpecBuilder;

    @Override
    public Specification<AgentWorklog> createSpec(AgentWorklogCriteria criteria) {
        Specification<AgentWorklog> spec = Specification.where(null);
        if (criteria != null) {
            spec = nonNull(spec, criteria.getId(), AgentWorklog_.id);
            spec = nonNull(spec, criteria.getSolution(), AgentWorklog_.solution);

            spec = nonNull(spec, criteria.getTakeStatus(), AgentWorklog_.takeStatus);
            spec = nonNull(spec, criteria.getCloseStatus(), AgentWorklog_.closeStatus);

            if (criteria.getWorkspace() != null) {
                AgentWorkspaceCriteria ws = criteria.getWorkspace();
                spec = nonNull(spec, ws.getId(), AgentWorklog_.workspace, AgentWorkspace_.id);
                spec = nonNull(spec, ws.getStatus(), AgentWorklog_.workspace, AgentWorkspace_.status);

                spec = nonNull(spec, ws.getUserId(), AgentWorklog_.workspace, AgentWorkspace_.agent, Agent_.userId);

                if (ws.getAgent() != null) {
                    AgentCriteria ag = ws.getAgent();
                    spec = nonNull(spec, ag.getId(), AgentWorklog_.workspace, AgentWorkspace_.agent, Agent_.id);
                    spec = nonNull(spec, ag.getNik(), AgentWorklog_.workspace, AgentWorkspace_.agent, Agent_.nik);
                    spec = nonNull(spec, ag.getUserId(), AgentWorklog_.workspace, AgentWorkspace_.agent, Agent_.userId);
                    spec = nonNull(spec, ag.getTelegram(), AgentWorklog_.workspace, AgentWorkspace_.agent, Agent_.telegramId);
                }

                if (ws.getTicket() != null) {
                    TicketCriteria tc = ws.getTicket();
                    spec = nonNull(spec, tc.getId(), AgentWorklog_.workspace, AgentWorkspace_.ticket, Ticket_.id);
                    spec = nonNull(spec, tc.getNo(), AgentWorklog_.workspace, AgentWorkspace_.ticket, Ticket_.no);
                    spec = nonNull(spec, tc.getWitel(), AgentWorklog_.workspace, AgentWorkspace_.ticket, Ticket_.witel);
                    spec = nonNull(spec, tc.getSto(), AgentWorklog_.workspace, AgentWorkspace_.ticket, Ticket_.sto);
                    spec = nonNull(spec, tc.getIncidentNo(), AgentWorklog_.workspace, AgentWorkspace_.ticket, Ticket_.incidentNo);
                    spec = nonNull(spec, tc.getServiceNo(), AgentWorklog_.workspace, AgentWorkspace_.ticket, Ticket_.serviceNo);
                    spec = nonNull(spec, tc.getStatus(), AgentWorklog_.workspace, AgentWorkspace_.ticket, Ticket_.status);
                    spec = nonNull(spec, tc.getSource(), AgentWorklog_.workspace, AgentWorkspace_.ticket, Ticket_.source);
                    spec = nonNull(spec, tc.getGaul(), AgentWorklog_.workspace, AgentWorkspace_.ticket, Ticket_.gaul);
                    spec = nonNull(spec, tc.getSenderId(), AgentWorklog_.workspace, AgentWorkspace_.ticket, Ticket_.senderId);
                    spec = nonNull(spec, tc.getSenderName(), AgentWorklog_.workspace, AgentWorkspace_.ticket, Ticket_.senderName);
                    spec = nonNull(spec, tc.getProduct(), AgentWorklog_.workspace, AgentWorkspace_.ticket, Ticket_.issue, Issue_.product);

                    spec = nonNull(spec, tc.getCreatedAt(), AgentWorklog_.workspace, AgentWorkspace_.ticket, Ticket_.createdAt);
                    spec = nonNull(spec, tc.getCreatedBy(), AgentWorklog_.workspace, AgentWorkspace_.ticket, Ticket_.createdBy);
                    spec = nonNull(spec, tc.getUpdatedAt(), AgentWorklog_.workspace, AgentWorkspace_.ticket, Ticket_.updatedAt);
                    spec = nonNull(spec, tc.getUpdatedBy(), AgentWorklog_.workspace, AgentWorkspace_.ticket, Ticket_.updatedBy);
                }
            }
        }
        return timestampSpec(spec, criteria);
    }
}

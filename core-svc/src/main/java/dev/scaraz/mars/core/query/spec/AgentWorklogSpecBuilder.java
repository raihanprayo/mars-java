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

    @Override
    public Specification<AgentWorklog> createSpec(AgentWorklogCriteria criteria) {
        SpecSingleChain<AgentWorklog> chain = chain();
        if (criteria != null) {
            chain.pick(AgentWorklog_.id, criteria.getId())
                    .pick(AgentWorklog_.solution, criteria.getSolution())
                    .pick(AgentWorklog_.takeStatus, criteria.getTakeStatus())
                    .pick(AgentWorklog_.closeStatus, criteria.getCloseStatus())
                    .extend(s -> timestampSpec(s, criteria));

            if (criteria.getWorkspace() != null) {
                AgentWorkspaceCriteria ws = criteria.getWorkspace();
                chain.pick(ws.getId(), r -> r.get(AgentWorklog_.workspace).get(AgentWorkspace_.id))
                        .pick(ws.getStatus(), r -> r.get(AgentWorklog_.workspace).get(AgentWorkspace_.status))
                        .pick(ws.getUserId(), r -> r.get(AgentWorklog_.workspace).get(AgentWorkspace_.agent).get(Agent_.userId));

                if (ws.getAgent() != null) {
                    AgentCriteria ag = ws.getAgent();
                    chain.pick(ag.getId(), r -> r.get(AgentWorklog_.workspace).get(AgentWorkspace_.agent).get(Agent_.id))
                            .pick(ag.getNik(), r -> r.get(AgentWorklog_.workspace).get(AgentWorkspace_.agent).get(Agent_.nik))
                            .pick(ag.getUserId(), r -> r.get(AgentWorklog_.workspace).get(AgentWorkspace_.agent).get(Agent_.userId))
                            .pick(ag.getTelegram(), r -> r.get(AgentWorklog_.workspace).get(AgentWorkspace_.agent).get(Agent_.telegramId));
                }

                if (ws.getTicket() != null) {
                    TicketCriteria tc = ws.getTicket();
                    chain.pick(tc.getId(), r -> r.get(AgentWorklog_.workspace).get(AgentWorkspace_.ticket).get(Ticket_.id))
                            .pick(tc.getNo(), r -> r.get(AgentWorklog_.workspace).get(AgentWorkspace_.ticket).get(Ticket_.no))
                            .pick(tc.getWitel(), r -> r.get(AgentWorklog_.workspace).get(AgentWorkspace_.ticket).get(Ticket_.witel))
                            .pick(tc.getSto(), r -> r.get(AgentWorklog_.workspace).get(AgentWorkspace_.ticket).get(Ticket_.sto))
                            .pick(tc.getIncidentNo(), r -> r.get(AgentWorklog_.workspace).get(AgentWorkspace_.ticket).get(Ticket_.incidentNo))
                            .pick(tc.getServiceNo(), r -> r.get(AgentWorklog_.workspace).get(AgentWorkspace_.ticket).get(Ticket_.serviceNo))
                            .pick(tc.getStatus(), r -> r.get(AgentWorklog_.workspace).get(AgentWorkspace_.ticket).get(Ticket_.status))
                            .pick(tc.getSource(), r -> r.get(AgentWorklog_.workspace).get(AgentWorkspace_.ticket).get(Ticket_.source))
                            .pick(tc.getGaul(), r -> r.get(AgentWorklog_.workspace).get(AgentWorkspace_.ticket).get(Ticket_.gaul))
                            .pick(tc.getSenderId(), r -> r.get(AgentWorklog_.workspace).get(AgentWorkspace_.ticket).get(Ticket_.senderId))
                            .pick(tc.getSenderName(), r -> r.get(AgentWorklog_.workspace).get(AgentWorkspace_.ticket).get(Ticket_.senderName))
                            .pick(tc.getProduct(), r -> r.get(AgentWorklog_.workspace).get(AgentWorkspace_.ticket).get(Ticket_.issue).get(Issue_.product));
                }
            }
        }
        return chain.specification();
    }
}

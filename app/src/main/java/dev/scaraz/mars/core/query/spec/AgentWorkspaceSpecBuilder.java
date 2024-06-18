package dev.scaraz.mars.core.query.spec;

import dev.scaraz.mars.common.query.AuditableSpec;
import dev.scaraz.mars.core.domain.order.AgentWorkspace;
import dev.scaraz.mars.core.domain.order.AgentWorkspace_;
import dev.scaraz.mars.core.domain.order.Agent_;
import dev.scaraz.mars.core.domain.order.Ticket_;
import dev.scaraz.mars.core.query.criteria.AgentCriteria;
import dev.scaraz.mars.core.query.criteria.AgentWorkspaceCriteria;
import dev.scaraz.mars.core.query.criteria.TicketCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgentWorkspaceSpecBuilder extends AuditableSpec<AgentWorkspace, AgentWorkspaceCriteria> {

    @Override
    public Specification<AgentWorkspace> createSpec(AgentWorkspaceCriteria criteria) {
        SpecChain<AgentWorkspace> chain = chain()
                .pick(AgentWorkspace_.id, criteria.getId())
                .pick(AgentWorkspace_.status, criteria.getStatus())
                .extend(s -> auditSpec(s, criteria));

        if (criteria.getAgent() != null) {
            AgentCriteria agent = criteria.getAgent();
            chain.pick(agent.getId(), path(AgentWorkspace_.agent, Agent_.id));
            chain.pick(agent.getNik(), path(AgentWorkspace_.agent, Agent_.nik));
            chain.pick(agent.getUserId(), path(AgentWorkspace_.agent, Agent_.userId));
            chain.pick(agent.getTelegram(), path(AgentWorkspace_.agent, Agent_.telegramId));
        }

        if (criteria.getTicket() != null) {
            TicketCriteria ticket = criteria.getTicket();
            chain.pick(ticket.getId(), path(AgentWorkspace_.ticket, Ticket_.id));
            chain.pick(ticket.getNo(), path(AgentWorkspace_.ticket, Ticket_.no));
            chain.pick(ticket.getCreatedAt(), path(AgentWorkspace_.ticket, Ticket_.createdAt));
        }

        return chain.specification();
    }

}

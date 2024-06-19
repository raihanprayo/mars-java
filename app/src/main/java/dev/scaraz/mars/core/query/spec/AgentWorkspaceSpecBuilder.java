package dev.scaraz.mars.core.query.spec;

import dev.scaraz.mars.common.query.AuditableSpec;
import dev.scaraz.mars.core.domain.credential.AccountTg_;
import dev.scaraz.mars.core.domain.credential.Account_;
import dev.scaraz.mars.core.domain.agent.AgentWorkspace;
import dev.scaraz.mars.core.domain.agent.AgentWorkspace_;
import dev.scaraz.mars.core.domain.order.Ticket_;
import dev.scaraz.mars.core.query.criteria.AgentWorkspaceCriteria;
import dev.scaraz.mars.core.query.criteria.TicketCriteria;
import dev.scaraz.mars.core.query.criteria.AccountCriteria;
import dev.scaraz.mars.core.query.criteria.AccountTgCriteria;
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

        if (criteria.getAccount() != null) {
            AccountCriteria agent = criteria.getAccount();
            chain.pick(agent.getId(), path(AgentWorkspace_.account, Account_.id));
            chain.pick(agent.getNik(), path(AgentWorkspace_.account, Account_.nik));

            if (agent.getTg() != null) {
                AccountTgCriteria tg = agent.getTg();
                chain.pick(tg.getId(), path(AgentWorkspace_.account, Account_.tg, AccountTg_.id));
            }
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

package dev.scaraz.mars.core.query.spec;

import dev.scaraz.mars.common.query.TimestampSpec;
import dev.scaraz.mars.core.domain.order.AgentWorkspace;
import dev.scaraz.mars.core.domain.order.AgentWorkspace_;
import dev.scaraz.mars.core.query.criteria.AgentWorkspaceCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgentWorkspaceSpecBuilder extends TimestampSpec<AgentWorkspace, AgentWorkspaceCriteria> {

    private final AgentSpecBuilder agentSpecBuilder;
    private final TicketSpecBuilder ticketSpecBuilder;

    @Override
    public Specification<AgentWorkspace> createSpec(AgentWorkspaceCriteria criteria) {
        SpecChain<AgentWorkspace> chain = chain()
                .pick(AgentWorkspace_.id, criteria.getId())
                .pick(AgentWorkspace_.status, criteria.getStatus());

        return chain.specification();
    }

}

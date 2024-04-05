package dev.scaraz.mars.core.query.spec;

import dev.scaraz.mars.common.query.AuditableSpec;
import dev.scaraz.mars.core.domain.order.AgentWorkspace;
import dev.scaraz.mars.core.domain.order.AgentWorkspace_;
import dev.scaraz.mars.core.query.criteria.AgentWorkspaceCriteria;
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

        return chain.specification();
    }

}

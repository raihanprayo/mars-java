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
        return auditSpec(spec, criteria);
    }



}

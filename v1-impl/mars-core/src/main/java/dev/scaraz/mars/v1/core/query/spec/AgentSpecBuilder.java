package dev.scaraz.mars.v1.core.query.spec;

import dev.scaraz.mars.common.query.AuditableSpec;
import dev.scaraz.mars.v1.core.domain.order.Agent;
import dev.scaraz.mars.v1.core.query.criteria.AgentCriteria;
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

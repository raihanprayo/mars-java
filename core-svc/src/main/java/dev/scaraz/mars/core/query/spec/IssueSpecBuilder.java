package dev.scaraz.mars.core.query.spec;

import dev.scaraz.mars.common.query.AuditableSpec;
import dev.scaraz.mars.core.domain.order.Issue;
import dev.scaraz.mars.core.domain.order.Issue_;
import dev.scaraz.mars.core.query.criteria.IssueCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class IssueSpecBuilder extends AuditableSpec<Issue, IssueCriteria> {

    @Override
    public Specification<Issue> createSpec(IssueCriteria criteria) {
        Specification<Issue> spec = Specification.where(null);
        if (criteria != null) {
            spec = nonNull(spec, criteria.getId(), Issue_.id);
            spec = nonNull(spec, criteria.getName(), Issue_.name);
            spec = nonNull(spec, criteria.getProduct(), Issue_.product);
        }
        return auditSpec(spec, criteria);
    }
}

package dev.scaraz.mars.core.service.query.spec;

import dev.scaraz.mars.common.query.AuditableSpec;
import dev.scaraz.mars.core.datasource.domain.Issue;
import dev.scaraz.mars.core.datasource.domain.Issue_;
import dev.scaraz.mars.core.web.criteria.IssueCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.metamodel.SingularAttribute;

@Component
public class IssueSpecBuilder extends AuditableSpec<Issue, IssueCriteria> {

    @Override
    public Specification<Issue> createSpec(IssueCriteria criteria) {
        Specification<Issue> spec = Specification.where(null);
        if (criteria != null) {
            spec = nonNull(spec, criteria.getId(), path(Issue_.id));
            spec = nonNull(spec, criteria.getName(), path(Issue_.name));
            spec = nonNull(spec, criteria.getProduct(), path(Issue_.product));
        }
        return auditSpec(spec, criteria);
    }

    public <E> Specification<E> createSpec(Specification<E> spec, SingularAttribute<E, Issue> join, IssueCriteria criteria) {
        if (criteria != null) {
            spec = nonNull(spec, criteria.getId(), r -> r.join(join).get(Issue_.id));
            spec = nonNull(spec, criteria.getName(), r -> r.join(join).get(Issue_.name));
            spec = nonNull(spec, criteria.getProduct(), r -> r.join(join).get(Issue_.product));
        }
        return spec;
    }

}

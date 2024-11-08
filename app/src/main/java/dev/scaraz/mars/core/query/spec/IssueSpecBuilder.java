package dev.scaraz.mars.core.query.spec;

import dev.scaraz.mars.common.query.AuditableSpec;
import dev.scaraz.mars.core.domain.symptom.Issue;
import dev.scaraz.mars.core.domain.symptom.Issue_;
import dev.scaraz.mars.core.query.criteria.IssueCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class IssueSpecBuilder extends AuditableSpec<Issue, IssueCriteria> {

    @Override
    public Specification<Issue> createSpec(IssueCriteria criteria) {
        return chain()
                .pick(Issue_.id, criteria.getId())
                .pick(Issue_.name, criteria.getName())
                .pick(Issue_.product, criteria.getProduct())
                .pick(Issue_.deleted, criteria.getDeleted())
                .extend(s -> auditSpec(s, criteria))
                .specification();
    }
}

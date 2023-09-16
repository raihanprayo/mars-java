package dev.scaraz.mars.app.witel.service.query.specification;

import dev.scaraz.mars.app.witel.domain.Issue;
import dev.scaraz.mars.app.witel.domain.Issue_;
import dev.scaraz.mars.app.witel.web.criteria.IssueCriteria;
import dev.scaraz.mars.common.query.AuditableSpec;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class IssueSpecBuilder extends AuditableSpec<Issue, IssueCriteria> {

    @Override
    public Specification<Issue> createSpec(IssueCriteria criteria) {
        return chain()
                .pick(Issue_.id, criteria.getId())
                .pick(Issue_.witel, criteria.getWitel())
                .pick(Issue_.code, criteria.getCode())
                .pick(Issue_.name, criteria.getName())
                .pick(Issue_.score, criteria.getScore())
                .pick(Issue_.deleted, criteria.getDeleted())
                .extend(s -> auditSpec(s, criteria))
                .specification();
    }

}

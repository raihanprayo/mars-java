package dev.scaraz.mars.core.query.spec;

import dev.scaraz.mars.common.query.AuditableSpec;
import dev.scaraz.mars.core.domain.credential.Group;
import dev.scaraz.mars.core.domain.credential.Group_;
import dev.scaraz.mars.core.query.criteria.GroupCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class GroupSpecBuilder extends AuditableSpec<Group, GroupCriteria> {

    @Override
    public Specification<Group> createSpec(GroupCriteria criteria) {
        Specification<Group> spec = Specification.where(null);
        if (criteria != null) {
            spec = nonNull(spec, criteria.getId(), Group_.id);
            spec = nonNull(spec, criteria.getName(), Group_.name);

            if (criteria.getParent() != null) {
                spec = nonNull(spec, criteria.getParent().getId(), Group_.parent, Group_.id);
                spec = nonNull(spec, criteria.getParent().getName(), Group_.parent, Group_.name);
            }
        }
        return auditSpec(spec, criteria);
    }

}

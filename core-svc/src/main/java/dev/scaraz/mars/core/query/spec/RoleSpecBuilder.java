package dev.scaraz.mars.core.query.spec;

import dev.scaraz.mars.common.query.AuditableSpec;
import dev.scaraz.mars.core.domain.credential.Group_;
import dev.scaraz.mars.core.domain.credential.Role;
import dev.scaraz.mars.core.domain.credential.Role_;
import dev.scaraz.mars.core.query.criteria.GroupCriteria;
import dev.scaraz.mars.core.query.criteria.RoleCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class RoleSpecBuilder extends AuditableSpec<Role, RoleCriteria> {

    @Override
    public Specification<Role> createSpec(RoleCriteria criteria) {
        Specification<Role> spec = Specification.where(null);
        if (criteria != null) {
            spec = nonNull(spec, criteria.getId(), Role_.id);
            spec = nonNull(spec, criteria.getName(), Role_.name);
            spec = nonNull(spec, criteria.getOrder(), Role_.order);
        }
        return auditSpec(spec, criteria);
    }

}

package dev.scaraz.mars.core.query.spec;

import dev.scaraz.mars.common.query.AuditableSpec;
import dev.scaraz.mars.core.domain.credential.Role;
import dev.scaraz.mars.core.domain.credential.Role_;
import dev.scaraz.mars.core.query.criteria.RoleCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class RoleSpecBuilder extends AuditableSpec<Role, RoleCriteria> {

    @Override
    public Specification<Role> createSpec(RoleCriteria criteria) {
        return chain()
                .pick(Role_.id, criteria.getId())
                .pick(Role_.name, criteria.getName())
                .pick(Role_.order, criteria.getOrder())
                .extend(s -> auditSpec(s, criteria))
                .specification();
    }

}

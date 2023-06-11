package dev.scaraz.mars.user.query.spec;

import dev.scaraz.mars.common.query.AuditableSpec;
import dev.scaraz.mars.common.utils.lambda.PluralSupplier;
import dev.scaraz.mars.user.domain.Role;
import dev.scaraz.mars.user.domain.Role_;
import dev.scaraz.mars.user.web.criteria.RoleCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class RoleSpecBuilder extends AuditableSpec<Role, RoleCriteria> {

    @Override
    public Specification<Role> createSpec(RoleCriteria criteria) {
        Specification<Role> spec = Specification.where(null);
        if (criteria != null) {
            spec = nonNull(spec, criteria.getId(), Role_.id);
            spec = nonNull(spec, criteria.getName(), Role_.name);
        }
        return auditSpec(spec, criteria);
    }

    public <E, C extends Collection<Role>> Specification<E> joinSpec(
            Specification<E> spec,
            RoleCriteria criteria,
            PluralSupplier<Role, E, C> plural
    ) {
        return chain(spec, plural)
                .and(Role_.id, criteria.getId())
                .and(Role_.name, criteria.getName())
                .extend(s -> auditSpec(s, criteria, plural))
                .specification();
    }

}

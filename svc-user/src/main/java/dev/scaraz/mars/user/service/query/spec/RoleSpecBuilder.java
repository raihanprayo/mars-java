package dev.scaraz.mars.user.service.query.spec;

import dev.scaraz.mars.common.query.AuditableSpec;
import dev.scaraz.mars.user.datasource.domain.Role;
import dev.scaraz.mars.user.datasource.domain.Role_;
import dev.scaraz.mars.user.web.criteria.RoleCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;

@Component
public class RoleSpecBuilder extends AuditableSpec<Role, RoleCriteria> {

    @Override
    public Specification<Role> createSpec(RoleCriteria criteria) {
        Specification<Role> spec = Specification.where(null);
        if (criteria != null) {
            spec = nonNull(spec, criteria.getId(), path(Role_.id));
            spec = nonNull(spec, criteria.getName(), path(Role_.name));
            spec = nonNull(spec, criteria.getOrder(), path(Role_.order));
        }

        return auditSpec(spec, criteria);
    }

    public <E> Specification<E> createSpec(Specification<E> spec, SingularAttribute<E, Role> join, RoleCriteria criteria) {
        if (criteria == null) return spec;
        spec = nonNull(spec, criteria.getId(), path(join, Role_.id));
        spec = nonNull(spec, criteria.getName(), path(join, Role_.name));
        spec = nonNull(spec, criteria.getOrder(), path(join, Role_.order));
        return spec;
    }

    public <E> Specification<E> createSpec(Specification<E> spec, SetAttribute<E, Role> join, RoleCriteria criteria) {
        if (criteria == null) return spec;
        spec = nonNull(spec, criteria.getId(), r -> r.join(join).get(Role_.id));
        spec = nonNull(spec, criteria.getName(), r -> r.join(join).get(Role_.name));
        spec = nonNull(spec, criteria.getOrder(), r -> r.join(join).get(Role_.order));
        return spec;
    }

}

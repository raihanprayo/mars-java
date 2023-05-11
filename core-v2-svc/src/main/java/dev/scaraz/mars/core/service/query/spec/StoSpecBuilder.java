package dev.scaraz.mars.core.service.query.spec;

import dev.scaraz.mars.common.utils.QueryBuilder;
import dev.scaraz.mars.core.datasource.domain.Sto;
import dev.scaraz.mars.core.datasource.domain.Sto_;
import dev.scaraz.mars.core.web.criteria.StoCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.metamodel.SingularAttribute;

@Component
public class StoSpecBuilder extends QueryBuilder<Sto, StoCriteria> {

    @Override
    public Specification<Sto> createSpec(StoCriteria criteria) {
        Specification<Sto> spec = Specification.where(null);
        if (criteria != null) {
            spec = nonNull(spec, criteria.getId(), path(Sto_.id));
            spec = nonNull(spec, criteria.getWitel(), path(Sto_.witel));
            spec = nonNull(spec, criteria.getDatel(), path(Sto_.datel));
            spec = nonNull(spec, criteria.getAlias(), path(Sto_.alias));
            spec = nonNull(spec, criteria.getName(), path(Sto_.name));
        }
        return spec;
    }

    public <E> Specification<E> createSpec(Specification<E> spec, SingularAttribute<E, Sto> join, StoCriteria criteria) {
        if (criteria != null) {
            spec = nonNull(spec, criteria.getId(), path(join, Sto_.id));
            spec = nonNull(spec, criteria.getWitel(), path(join, Sto_.witel));
            spec = nonNull(spec, criteria.getDatel(), path(join, Sto_.datel));
            spec = nonNull(spec, criteria.getAlias(), path(join, Sto_.alias));
            spec = nonNull(spec, criteria.getName(), path(join, Sto_.name));
        }
        return spec;
    }

}

package dev.scaraz.mars.user.query.spec;

import dev.scaraz.mars.common.utils.QueryBuilder;
import dev.scaraz.mars.user.domain.Sto;
import dev.scaraz.mars.user.domain.Sto_;
import dev.scaraz.mars.user.web.criteria.StoCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class StoSpecBuilder extends QueryBuilder<Sto, StoCriteria> {
    @Override
    public Specification<Sto> createSpec(StoCriteria criteria) {
        Specification<Sto> spec = Specification.where(null);
        if (criteria != null) {
            spec = nonNull(spec, criteria.getId(), Sto_.id);
            spec = nonNull(spec, criteria.getName(), Sto_.name);
            spec = nonNull(spec, criteria.getAlias(), Sto_.alias);
            spec = nonNull(spec, criteria.getDatel(), Sto_.datel);
            spec = nonNull(spec, criteria.getWitel(), Sto_.witel);
        }
        return spec;
    }
}

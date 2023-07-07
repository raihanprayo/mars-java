package dev.scaraz.mars.v1.core.query.spec;

import dev.scaraz.mars.common.utils.QueryBuilder;
import dev.scaraz.mars.v1.core.domain.order.Sto;
import dev.scaraz.mars.core.domain.order.Sto_;
import dev.scaraz.mars.v1.core.query.criteria.StoCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class StoSpecBuilder extends QueryBuilder<Sto, StoCriteria> {
    @Override
    public Specification<Sto> createSpec(StoCriteria criteria) {
        return chain()
                .pick(Sto_.id, criteria.getId())
                .pick(Sto_.witel, criteria.getWitel())
                .pick(Sto_.datel, criteria.getDatel())
                .pick(Sto_.alias, criteria.getAlias())
                .pick(Sto_.name, criteria.getName())
                .specification();
    }
}

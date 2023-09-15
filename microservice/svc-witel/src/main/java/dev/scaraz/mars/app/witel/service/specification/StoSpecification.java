package dev.scaraz.mars.app.witel.service.specification;

import dev.scaraz.mars.app.witel.domain.Sto;
import dev.scaraz.mars.app.witel.domain.Sto_;
import dev.scaraz.mars.app.witel.web.criteria.StoCriteria;
import dev.scaraz.mars.common.utils.QueryBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class StoSpecification extends QueryBuilder<Sto, StoCriteria> {
    @Override
    public Specification<Sto> createSpec(StoCriteria criteria) {
        return chain()
                .pick(Sto_.id, criteria.getId())
                .pick(Sto_.name, criteria.getName())
                .pick(Sto_.alias, criteria.getAlias())
                .pick(Sto_.datel, criteria.getDatel())
                .pick(Sto_.witel, criteria.getWitel())
                .specification();
    }
}

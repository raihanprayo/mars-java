package dev.scaraz.mars.core.v2.query.spec;

import dev.scaraz.mars.common.query.AuditableSpec;
import dev.scaraz.mars.core.v2.domain.app.Sto;
import dev.scaraz.mars.core.v2.domain.app.Sto_;
import dev.scaraz.mars.core.v2.web.criteria.StoCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class StoSpecification extends AuditableSpec<Sto, StoCriteria> {
    @Override
    public Specification<Sto> createSpec(StoCriteria criteria) {
        return chain()
                .pick(Sto_.id, criteria.getId())
                .pick(Sto_.name, criteria.getName())
                .pick(Sto_.witel, criteria.getWitel())
                .pick(Sto_.datel, criteria.getDatel())
                .extend(s -> auditSpec(s, criteria))
                .specification();
    }
}

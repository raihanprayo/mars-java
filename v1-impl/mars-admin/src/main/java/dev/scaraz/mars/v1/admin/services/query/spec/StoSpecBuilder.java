package dev.scaraz.mars.v1.admin.services.query.spec;

import dev.scaraz.mars.v1.admin.domain.app.Sto;
import dev.scaraz.mars.v1.admin.domain.app.Sto_;
import dev.scaraz.mars.v1.admin.web.criteria.StoCriteria;
import dev.scaraz.mars.common.query.AuditableSpec;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class StoSpecBuilder extends AuditableSpec<Sto, StoCriteria> {

    @Override
    public Specification<Sto> createSpec(StoCriteria criteria) {
        return chain()
                .pick(Sto_.code, criteria.getCode())
                .pick(Sto_.name, criteria.getName())
                .pick(Sto_.witel, criteria.getWitel())
                .pick(Sto_.datel, criteria.getDatel())
                .extend(s -> auditSpec(s, criteria))
                .specification();
    }

}

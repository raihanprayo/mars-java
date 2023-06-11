package dev.scaraz.mars.user.query.spec;

import dev.scaraz.mars.common.query.TimestampSpec;
import dev.scaraz.mars.user.domain.db.Sto;
import dev.scaraz.mars.user.domain.db.Sto_;
import dev.scaraz.mars.user.web.criteria.StoCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class StoSpecBuilder extends TimestampSpec<Sto, StoCriteria> {

    @Override
    public Specification<Sto> createSpec(StoCriteria criteria) {
        return chain()
                .and(Sto_.id, criteria.getId())
                .and(Sto_.name, criteria.getName())
                .and(Sto_.datel, criteria.getDatel())
                .and(Sto_.witel, criteria.getWitel())
                .extend(s -> timestampSpec(s, criteria))
                .specification();
    }
}

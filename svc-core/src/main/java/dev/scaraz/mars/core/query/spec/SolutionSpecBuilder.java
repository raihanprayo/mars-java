package dev.scaraz.mars.core.query.spec;

import dev.scaraz.mars.common.query.TimestampSpec;
import dev.scaraz.mars.core.domain.db.Solution;
import dev.scaraz.mars.core.domain.db.Solution_;
import dev.scaraz.mars.core.web.criteria.SolutionCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class SolutionSpecBuilder extends TimestampSpec<Solution, SolutionCriteria> {

    @Override
    public Specification<Solution> createSpec(SolutionCriteria criteria) {
        return chain()
                .pick(Solution_.id, criteria.getId())
                .pick(Solution_.product, criteria.getProduct())
                .pick(Solution_.name, criteria.getName())
                .extend(s -> timestampSpec(s, criteria))
                .specification();
    }

}

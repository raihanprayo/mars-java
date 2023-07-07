package dev.scaraz.mars.core.v2.query.spec;

import dev.scaraz.mars.common.query.TimestampSpec;
import dev.scaraz.mars.core.v2.domain.order.Solution;
import dev.scaraz.mars.core.v2.domain.order.Solution_;
import dev.scaraz.mars.core.v2.web.criteria.SolutionCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class SolutionSpecification extends TimestampSpec<Solution, SolutionCriteria> {
    @Override
    public Specification<Solution> createSpec(SolutionCriteria criteria) {
        return chain()
                .pick(Solution_.id, criteria.getId())
                .pick(Solution_.name, criteria.getName())
                .pick(Solution_.description, criteria.getDescription())
                .pick(Solution_.product, criteria.getProduct())
                .extend(s -> timestampSpec(s, criteria))
                .specification();
    }
}

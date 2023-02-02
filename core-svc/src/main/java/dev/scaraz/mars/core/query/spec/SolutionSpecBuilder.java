package dev.scaraz.mars.core.query.spec;

import dev.scaraz.mars.common.query.TimestampSpec;
import dev.scaraz.mars.core.domain.order.Solution;
import dev.scaraz.mars.core.domain.order.Solution_;
import dev.scaraz.mars.core.query.criteria.SolutionCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class SolutionSpecBuilder extends TimestampSpec<Solution, SolutionCriteria> {

    @Override
    public Specification<Solution> createSpec(SolutionCriteria criteria) {
        Specification<Solution> spec = Specification.where(null);
        if (criteria != null) {
            spec = nonNull(spec, criteria.getId(), Solution_.id);
            spec = nonNull(spec, criteria.getName(), Solution_.name);
            spec = nonNull(spec, criteria.getProduct(), Solution_.product);
        }
        return timestampSpec(spec, criteria);
    }

}

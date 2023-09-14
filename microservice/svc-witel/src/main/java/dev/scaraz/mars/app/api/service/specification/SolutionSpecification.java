package dev.scaraz.mars.app.api.service.specification;

import dev.scaraz.mars.app.api.domain.Solution;
import dev.scaraz.mars.app.api.domain.Solution_;
import dev.scaraz.mars.app.api.web.criteria.SolutionCriteria;
import dev.scaraz.mars.common.query.TimestampSpec;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class SolutionSpecification extends TimestampSpec<Solution, SolutionCriteria> {
    @Override
    public Specification<Solution> createSpec(SolutionCriteria criteria) {
        return chain()
                .pick(Solution_.id, criteria.getId())
                .pick(Solution_.name, criteria.getName())
                .pick(Solution_.product, criteria.getProduct())
                .extend(s -> timestampSpec(s, criteria))
                .specification();
    }
}

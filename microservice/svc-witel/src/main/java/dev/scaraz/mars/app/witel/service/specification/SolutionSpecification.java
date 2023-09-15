package dev.scaraz.mars.app.witel.service.specification;

import dev.scaraz.mars.app.witel.domain.Solution;
import dev.scaraz.mars.app.witel.domain.Solution_;
import dev.scaraz.mars.app.witel.web.criteria.SolutionCriteria;
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

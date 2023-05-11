package dev.scaraz.mars.core.service.query.spec;

import dev.scaraz.mars.common.query.TimestampSpec;
import dev.scaraz.mars.core.datasource.domain.Solution;
import dev.scaraz.mars.core.datasource.domain.Solution_;
import dev.scaraz.mars.core.web.criteria.SolutionCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.metamodel.SingularAttribute;

@Component
public class SolutionSpecBuilder extends TimestampSpec<Solution, SolutionCriteria> {

    @Override
    public Specification<Solution> createSpec(SolutionCriteria criteria) {
        Specification<Solution> spec = Specification.where(null);
        if (criteria != null) {
            spec = nonNull(spec, criteria.getId(), path(Solution_.id));
            spec = nonNull(spec, criteria.getName(), path(Solution_.name));
            spec = nonNull(spec, criteria.getProduct(), path(Solution_.product));
        }
        return timestampSpec(spec, criteria);
    }

    public <E> Specification<E> createSpec(Specification<E> spec, SingularAttribute<E, Solution> join, SolutionCriteria criteria) {
        if (criteria != null) {
            spec = nonNull(spec, criteria.getId(), path(join, Solution_.id));
            spec = nonNull(spec, criteria.getName(), path(join, Solution_.name));
            spec = nonNull(spec, criteria.getProduct(), path(join, Solution_.product));
        }
        return spec;
    }

}

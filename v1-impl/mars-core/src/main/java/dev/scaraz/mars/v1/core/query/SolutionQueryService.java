package dev.scaraz.mars.v1.core.query;

import dev.scaraz.mars.v1.core.domain.order.Solution;
import dev.scaraz.mars.v1.core.query.criteria.SolutionCriteria;

public interface SolutionQueryService extends BaseQueryService<Solution, SolutionCriteria> {
    Solution findById(long id);
}

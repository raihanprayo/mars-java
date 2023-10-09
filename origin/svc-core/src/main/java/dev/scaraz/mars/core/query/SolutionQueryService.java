package dev.scaraz.mars.core.query;

import dev.scaraz.mars.core.domain.order.Solution;
import dev.scaraz.mars.core.query.criteria.SolutionCriteria;

public interface SolutionQueryService extends BaseQueryService<Solution, SolutionCriteria> {
    Solution findById(long id);
}

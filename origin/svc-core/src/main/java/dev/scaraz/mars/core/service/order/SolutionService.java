package dev.scaraz.mars.core.service.order;

import dev.scaraz.mars.core.domain.order.Solution;

import java.util.List;

public interface SolutionService {
    Solution save(Solution solution);

    void deleteByIds(List<Long> ids);

    Solution update(long id, Solution update);
}

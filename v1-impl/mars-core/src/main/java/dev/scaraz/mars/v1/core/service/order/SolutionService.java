package dev.scaraz.mars.v1.core.service.order;

import dev.scaraz.mars.v1.core.domain.order.Solution;

import java.util.List;

public interface SolutionService {
    Solution save(Solution solution);

    void deleteByIds(List<Long> ids);
}

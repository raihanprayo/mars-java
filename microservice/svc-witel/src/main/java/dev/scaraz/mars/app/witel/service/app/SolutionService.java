package dev.scaraz.mars.app.witel.service.app;


import dev.scaraz.mars.app.witel.domain.Solution;

import java.util.List;

public interface SolutionService {
    Solution save(Solution solution);

    void deleteByIds(List<String> ids);
}

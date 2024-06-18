package dev.scaraz.mars.core.service.order.impl;

import dev.scaraz.mars.core.domain.symptom.Solution;
import dev.scaraz.mars.core.query.SolutionQueryService;
import dev.scaraz.mars.core.repository.db.order.SolutionRepo;
import dev.scaraz.mars.core.service.order.AgentService;
import dev.scaraz.mars.core.service.order.SolutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor

@Service
public class SolutionServiceImpl implements SolutionService {

    private final SolutionRepo repo;
    private final SolutionQueryService queryService;

    private final AgentService agentService;

    @Override
    public Solution save(Solution solution) {
        return repo.save(solution);
    }

    @Override
    @Transactional
    public void deleteByIds(List<Long> ids) {
        repo.deleteAllByIdInAndDeleteableIsTrue(ids);
    }

    @Override
    @Transactional
    public Solution update(long id, Solution update) {
        Solution solution = queryService.findById(id);
        BeanUtils.copyProperties(update, solution, "id", "createdAt", "updatedAt");
        solution = save(solution);
        agentService.updateWorlklogSolution(solution);
        return solution;
    }

}

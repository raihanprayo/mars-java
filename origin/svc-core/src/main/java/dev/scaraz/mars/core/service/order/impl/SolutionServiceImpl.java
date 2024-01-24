package dev.scaraz.mars.core.service.order.impl;

import dev.scaraz.mars.core.domain.order.Solution;
import dev.scaraz.mars.core.query.SolutionQueryService;
import dev.scaraz.mars.core.repository.db.order.SolutionRepo;
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

    @Override
    public Solution save(Solution solution) {
        return repo.save(solution);
    }

    @Override
    @Transactional
    public void deleteByIds(List<Long> ids) {
        repo.deleteAllById(ids);
    }

    @Override
    @Transactional
    public Solution update(long id, Solution update) {
        Solution solution = queryService.findById(id);
        BeanUtils.copyProperties(update, solution, "id");
        return save(solution);
    }

}

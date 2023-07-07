package dev.scaraz.mars.v1.core.service.order.impl;

import dev.scaraz.mars.v1.core.domain.order.Solution;
import dev.scaraz.mars.v1.core.repository.order.SolutionRepo;
import dev.scaraz.mars.v1.core.service.order.SolutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor

@Service
public class SolutionServiceImpl implements SolutionService {

    private final SolutionRepo repo;

    @Override
    public Solution save(Solution solution) {
        return repo.save(solution);
    }

    @Override
    @Transactional
    public void deleteByIds(List<Long> ids) {
        repo.deleteAllById(ids);
    }

}

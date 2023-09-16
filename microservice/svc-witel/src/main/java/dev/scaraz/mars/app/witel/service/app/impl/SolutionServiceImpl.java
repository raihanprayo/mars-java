package dev.scaraz.mars.app.witel.service.app.impl;

import dev.scaraz.mars.app.witel.domain.Solution;
import dev.scaraz.mars.app.witel.repository.SolutionRepo;
import dev.scaraz.mars.app.witel.service.app.SolutionService;
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
    public void deleteByIds(List<String> ids) {
        repo.deleteAllById(ids);
    }

}

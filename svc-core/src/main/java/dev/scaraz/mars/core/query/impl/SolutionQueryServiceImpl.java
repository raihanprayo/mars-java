package dev.scaraz.mars.core.query.impl;

import dev.scaraz.mars.core.domain.db.Solution;
import dev.scaraz.mars.core.query.SolutionQueryService;
import dev.scaraz.mars.core.query.spec.SolutionSpecBuilder;
import dev.scaraz.mars.core.repository.db.SolutionRepo;
import dev.scaraz.mars.core.web.criteria.SolutionCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SolutionQueryServiceImpl implements SolutionQueryService {

    private final SolutionRepo repo;
    private final SolutionSpecBuilder specBuilder;

    @Override
    public List<Solution> findAll() {
        return repo.findAll();
    }

    @Override
    public Page<Solution> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    public List<Solution> findAll(SolutionCriteria criteria) {
        return repo.findAll(specBuilder.createSpec(criteria));
    }

    @Override
    public Page<Solution> findAll(SolutionCriteria criteria, Pageable pageable) {
        return repo.findAll(specBuilder.createSpec(criteria), pageable);
    }
}

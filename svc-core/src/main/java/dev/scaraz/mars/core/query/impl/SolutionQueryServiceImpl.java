package dev.scaraz.mars.core.query.impl;

import dev.scaraz.mars.common.exception.web.NotFoundException;
import dev.scaraz.mars.core.domain.order.Solution;
import dev.scaraz.mars.core.query.SolutionQueryService;
import dev.scaraz.mars.core.query.criteria.SolutionCriteria;
import dev.scaraz.mars.core.query.spec.SolutionSpecBuilder;
import dev.scaraz.mars.core.repository.db.order.SolutionRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor

@Service
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

    @Override
    public long count() {
        return repo.count();
    }

    @Override
    public long count(SolutionCriteria criteria) {
        return repo.count(specBuilder.createSpec(criteria));
    }

    @Override
    public Solution findById(long id) {
        return repo.findById(id)
                .orElseThrow(() -> NotFoundException.entity(Solution.class, "id", id));
    }

}

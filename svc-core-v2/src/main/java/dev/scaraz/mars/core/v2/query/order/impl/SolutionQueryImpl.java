package dev.scaraz.mars.core.v2.query.order.impl;

import dev.scaraz.mars.core.v2.domain.order.Solution;
import dev.scaraz.mars.core.v2.query.order.SolutionQuery;
import dev.scaraz.mars.core.v2.query.spec.SolutionSpecification;
import dev.scaraz.mars.core.v2.repository.db.order.SolutionRepo;
import dev.scaraz.mars.core.v2.web.criteria.SolutionCriteria;
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
public class SolutionQueryImpl implements SolutionQuery {

    private final SolutionRepo repo;
    private final SolutionSpecification specification;

    @Override
    public List<Solution> findAll() {
        return repo.findAll();
    }

    @Override
    public List<Solution> findAll(SolutionCriteria criteria) {
        return repo.findAll(specification.createSpec(criteria));
    }

    @Override
    public Page<Solution> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    public Page<Solution> findAll(SolutionCriteria criteria, Pageable pageable) {
        return repo.findAll(specification.createSpec(criteria), pageable);
    }

    @Override
    public long count() {
        return repo.count();
    }

    @Override
    public long count(SolutionCriteria criteria) {
        return repo.count(specification.createSpec(criteria));
    }
}

package dev.scaraz.mars.core.v2.query.app.impl;

import dev.scaraz.mars.core.v2.domain.app.Sto;
import dev.scaraz.mars.core.v2.query.app.StoQueryService;
import dev.scaraz.mars.core.v2.query.spec.StoSpecification;
import dev.scaraz.mars.core.v2.repository.db.app.StoRepo;
import dev.scaraz.mars.core.v2.web.criteria.StoCriteria;
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
public class StoQueryServiceImpl implements StoQueryService {

    private final StoRepo repo;
    private final StoSpecification specification;

    @Override
    public List<Sto> findAll() {
        return repo.findAll();
    }

    @Override
    public List<Sto> findAll(StoCriteria criteria) {
        return repo.findAll(specification.createSpec(criteria));
    }

    @Override
    public Page<Sto> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    public Page<Sto> findAll(StoCriteria criteria, Pageable pageable) {
        return repo.findAll(specification.createSpec(criteria), pageable);
    }

    @Override
    public long count() {
        return repo.count();
    }

    @Override
    public long count(StoCriteria criteria) {
        return repo.count(specification.createSpec(criteria));
    }
}

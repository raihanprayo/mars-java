package dev.scaraz.mars.v1.core.query.impl;

import dev.scaraz.mars.v1.core.domain.order.Sto;
import dev.scaraz.mars.v1.core.query.StoQueryService;
import dev.scaraz.mars.v1.core.query.criteria.StoCriteria;
import dev.scaraz.mars.v1.core.query.spec.StoSpecBuilder;
import dev.scaraz.mars.v1.core.repository.order.StoRepo;
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
    private final StoSpecBuilder specBuilder;

    @Override
    public List<Sto> findAll() {
        return repo.findAll();
    }

    @Override
    public Page<Sto> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    public List<Sto> findAll(StoCriteria criteria) {
        return repo.findAll(specBuilder.createSpec(criteria));
    }

    @Override
    public Page<Sto> findAll(StoCriteria criteria, Pageable pageable) {
        return repo.findAll(specBuilder.createSpec(criteria), pageable);
    }

    @Override
    public long count() {
        return repo.count();
    }

    @Override
    public long count(StoCriteria criteria) {
        return repo.count(specBuilder.createSpec(criteria));
    }
}

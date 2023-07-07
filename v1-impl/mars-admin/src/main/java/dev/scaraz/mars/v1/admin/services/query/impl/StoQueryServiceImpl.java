package dev.scaraz.mars.v1.admin.services.query.impl;

import dev.scaraz.mars.v1.admin.domain.app.Sto;
import dev.scaraz.mars.v1.admin.repository.db.app.StoRepo;
import dev.scaraz.mars.v1.admin.services.query.StoQueryService;
import dev.scaraz.mars.v1.admin.services.query.spec.StoSpecBuilder;
import dev.scaraz.mars.v1.admin.web.criteria.StoCriteria;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
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
}

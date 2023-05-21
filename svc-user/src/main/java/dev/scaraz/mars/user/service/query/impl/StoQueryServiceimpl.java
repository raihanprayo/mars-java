package dev.scaraz.mars.user.service.query.impl;

import dev.scaraz.mars.user.datasource.domain.Sto;
import dev.scaraz.mars.user.datasource.repo.StoRepo;
import dev.scaraz.mars.user.service.query.StoQueryService;
import dev.scaraz.mars.user.service.query.spec.StoSpecBuilder;
import dev.scaraz.mars.user.web.criteria.StoCriteria;
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
public class StoQueryServiceimpl implements StoQueryService {

    private final StoRepo repo;
    private final StoSpecBuilder builder;

    @Override
    public List<Sto> findAll(StoCriteria criteria) {
        return repo.findAll(builder.createSpec(criteria));
    }

    @Override
    public Page<Sto> findAll(StoCriteria criteria, Pageable pageable) {
        return repo.findAll(
                builder.createSpec(criteria),
                pageable
        );
    }

}

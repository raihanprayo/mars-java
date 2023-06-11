package dev.scaraz.mars.user.query.impl;

import dev.scaraz.mars.common.exception.web.NotFoundException;
import dev.scaraz.mars.user.domain.db.Sto;
import dev.scaraz.mars.user.query.StoQueryService;
import dev.scaraz.mars.user.query.spec.StoSpecBuilder;
import dev.scaraz.mars.user.repository.db.StoRepo;
import dev.scaraz.mars.user.web.criteria.StoCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
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
    public Sto findByIdOrName(String idOrName) {
        return repo.findByIdOrName(idOrName, idOrName)
                .orElseThrow(() -> NotFoundException.entity(Sto.class, "id/name", idOrName));
    }

}

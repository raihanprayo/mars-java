package dev.scaraz.mars.core.query.impl;

import dev.scaraz.mars.core.domain.credential.AccountEvent;
import dev.scaraz.mars.core.query.AccountEventQueryService;
import dev.scaraz.mars.core.query.criteria.AccountEventCriteria;
import dev.scaraz.mars.core.query.spec.AccountEventSpecBuilder;
import dev.scaraz.mars.core.repository.db.credential.AccountEventRepo;
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
public class AccountEventQueryServiceImpl implements AccountEventQueryService {
    private final AccountEventRepo repo;
    private final AccountEventSpecBuilder specBuilder;

    @Override
    public List<AccountEvent> findAll() {
        return repo.findAll();
    }

    @Override
    public Page<AccountEvent> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    public List<AccountEvent> findAll(AccountEventCriteria criteria) {
        return repo.findAll(specBuilder.createSpec(criteria));
    }

    @Override
    public Page<AccountEvent> findAll(AccountEventCriteria criteria, Pageable pageable) {
        return repo.findAll(specBuilder.createSpec(criteria), pageable);
    }

    @Override
    public long count() {
        return repo.count();
    }

    @Override
    public long count(AccountEventCriteria criteria) {
        return repo.count(specBuilder.createSpec(criteria));
    }
}

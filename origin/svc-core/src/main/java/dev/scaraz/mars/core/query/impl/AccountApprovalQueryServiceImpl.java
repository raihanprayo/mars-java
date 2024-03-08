package dev.scaraz.mars.core.query.impl;

import dev.scaraz.mars.core.domain.credential.AccountApproval;
import dev.scaraz.mars.core.query.AccountApprovalQueryService;
import dev.scaraz.mars.core.query.criteria.AccountApprovalCriteria;
import dev.scaraz.mars.core.query.spec.AccountApprovalSpecBuilder;
import dev.scaraz.mars.core.repository.db.credential.AccountApprovalRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountApprovalQueryServiceImpl implements AccountApprovalQueryService {

    private final AccountApprovalRepo repo;
    private final AccountApprovalSpecBuilder specBuilder;

    @Override
    public Page<AccountApproval> findAll(AccountApprovalCriteria criteria, Pageable pageable) {
        return repo.findAll(specBuilder.createSpec(criteria), pageable);
    }

}

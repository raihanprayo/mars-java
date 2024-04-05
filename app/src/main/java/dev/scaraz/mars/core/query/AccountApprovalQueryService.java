package dev.scaraz.mars.core.query;

import dev.scaraz.mars.core.domain.credential.AccountApproval;
import dev.scaraz.mars.core.query.criteria.AccountApprovalCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccountApprovalQueryService {
    Page<AccountApproval> findAll(AccountApprovalCriteria criteria, Pageable pageable);
}

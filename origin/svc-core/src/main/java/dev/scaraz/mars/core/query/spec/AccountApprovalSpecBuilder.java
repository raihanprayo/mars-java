package dev.scaraz.mars.core.query.spec;

import dev.scaraz.mars.common.utils.QueryBuilder;
import dev.scaraz.mars.core.domain.credential.AccountApproval;
import dev.scaraz.mars.core.domain.credential.AccountApproval_;
import dev.scaraz.mars.core.query.criteria.AccountApprovalCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class AccountApprovalSpecBuilder extends QueryBuilder<AccountApproval, AccountApprovalCriteria> {
    @Override
    public Specification<AccountApproval> createSpec(AccountApprovalCriteria criteria) {
        return chain()
                .pick(AccountApproval_.id, criteria.getId())
                .pick(AccountApproval_.no, criteria.getNo())
                .pick(AccountApproval_.status, criteria.getStatus())
                .pick(AccountApproval_.name, criteria.getName())
                .pick(AccountApproval_.nik, criteria.getNik())
                .pick(AccountApproval_.phone, criteria.getPhone())
                .pick(AccountApproval_.witel, criteria.getWitel())
                .pick(AccountApproval_.sto, criteria.getSto())
                .specification();
    }
}

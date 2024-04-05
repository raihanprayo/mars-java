package dev.scaraz.mars.core.query.spec;

import dev.scaraz.mars.common.query.TimestampSpec;
import dev.scaraz.mars.core.domain.credential.AccountApproval;
import dev.scaraz.mars.core.domain.credential.AccountApproval_;
import dev.scaraz.mars.core.query.criteria.AccountApprovalCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class AccountApprovalSpecBuilder extends TimestampSpec<AccountApproval, AccountApprovalCriteria> {
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
                .extend(s -> timestampSpec(s, criteria))
                .specification();
    }
}

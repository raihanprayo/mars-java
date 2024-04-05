package dev.scaraz.mars.core.query.spec;

import dev.scaraz.mars.common.utils.QueryBuilder;
import dev.scaraz.mars.core.domain.credential.AccountEvent;
import dev.scaraz.mars.core.domain.credential.AccountEvent_;
import dev.scaraz.mars.core.query.criteria.AccountEventCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class AccountEventSpecBuilder extends QueryBuilder<AccountEvent, AccountEventCriteria> {
    @Override
    public Specification<AccountEvent> createSpec(AccountEventCriteria criteria) {
        return chain()
                .pick(AccountEvent_.id, criteria.getId())
                .pick(AccountEvent_.type, criteria.getType())
                .pick(AccountEvent_.createdAt, criteria.getCreatedAt())
                .pick(AccountEvent_.createdBy, criteria.getCreatedBy())
                .specification();
    }
}

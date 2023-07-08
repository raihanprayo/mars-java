package dev.scaraz.mars.core.query.spec;

import dev.scaraz.mars.common.query.AuditableSpec;
import dev.scaraz.mars.core.domain.credential.*;
import dev.scaraz.mars.core.query.criteria.RoleCriteria;
import dev.scaraz.mars.core.query.criteria.UserCriteria;
import dev.scaraz.mars.core.query.criteria.UserTgCriteria;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserSpecBuilder extends AuditableSpec<Account, UserCriteria> {

    @Override
    public Specification<Account> createSpec(UserCriteria criteria) {
        SpecChain<Account> chain = chain();

        if (criteria != null) {
            chain.pick(Account_.id, criteria.getId())
                    .pick(Account_.nik, criteria.getNik())
                    .pick(Account_.name, criteria.getName())
                    .pick(Account_.phone, criteria.getPhone())
                    .pick(Account_.email, criteria.getEmail())
                    .pick(Account_.witel, criteria.getWitel())
                    .pick(Account_.sto, criteria.getSto())
                    .pick(Account_.active, criteria.getActive())
                    .extend(s -> auditSpec(s, criteria));

            if (criteria.getTg() != null) {
                UserTgCriteria tg = criteria.getTg();
                chain.pick(tg.getId(), r -> r.get(Account_.tg).get(AccountTg_.id))
                        .pick(tg.getUsername(), r -> r.get(Account_.tg).get(AccountTg_.username));
            }

            if (criteria.getRoles() != null) {
                RoleCriteria roles = criteria.getRoles();
                chain.pick(roles.getId(), r -> r.join(Account_.roles).get(Role_.id))
                        .pick(roles.getName(), r -> r.join(Account_.roles).get(Role_.name));
            }
        }

        return chain.specification();
    }

}

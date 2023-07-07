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
public class UserSpecBuilder extends AuditableSpec<User, UserCriteria> {

    @Override
    public Specification<User> createSpec(UserCriteria criteria) {
        SpecChain<User> chain = chain();

        if (criteria != null) {
            chain.pick(User_.id, criteria.getId())
                    .pick(User_.nik, criteria.getNik())
                    .pick(User_.name, criteria.getName())
                    .pick(User_.phone, criteria.getPhone())
                    .pick(User_.email, criteria.getEmail())
                    .pick(User_.witel, criteria.getWitel())
                    .pick(User_.sto, criteria.getSto())
                    .pick(User_.active, criteria.getActive())
                    .extend(s -> auditSpec(s, criteria));

            if (criteria.getTg() != null) {
                UserTgCriteria tg = criteria.getTg();
                chain.pick(tg.getId(), r -> r.get(User_.tg).get(UserTg_.id))
                        .pick(tg.getUsername(), r -> r.get(User_.tg).get(UserTg_.username));
            }

            if (criteria.getRoles() != null) {
                RoleCriteria roles = criteria.getRoles();
                chain.pick(roles.getId(), r -> r.join(User_.roles).get(Role_.id))
                        .pick(roles.getName(), r -> r.join(User_.roles).get(Role_.name));
            }
        }

        return chain.specification();
    }

}

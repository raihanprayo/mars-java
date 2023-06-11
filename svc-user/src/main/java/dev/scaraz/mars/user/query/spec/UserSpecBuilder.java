package dev.scaraz.mars.user.query.spec;

import dev.scaraz.mars.common.query.AuditableSpec;
import dev.scaraz.mars.user.domain.db.MarsUser;
import dev.scaraz.mars.user.domain.db.MarsUser_;
import dev.scaraz.mars.user.web.criteria.UserCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSpecBuilder extends AuditableSpec<MarsUser, UserCriteria> {

    private final RoleSpecBuilder roleSpecBuilder;

    @Override
    public Specification<MarsUser> createSpec(UserCriteria criteria) {
        return chain()
                .and(MarsUser_.id, criteria.getId())
                .and(MarsUser_.name, criteria.getName())
                .and(MarsUser_.nik, criteria.getNik())
                .and(MarsUser_.telegram, criteria.getTelegram())
                .and(MarsUser_.witel, criteria.getWitel())
                .and(MarsUser_.sto, criteria.getSto())
                .and(MarsUser_.phone, criteria.getPhone())
                .and(MarsUser_.email, criteria.getEmail())
                .and(MarsUser_.enabled, criteria.getEnabled())
                .extend(s -> roleSpecBuilder.joinSpec(s, criteria.getRoles(), r -> r.join(MarsUser_.roles)))
                .extend(s -> auditSpec(s, criteria))
                .specification();
    }

}

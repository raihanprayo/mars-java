package dev.scaraz.mars.user.service.query.spec;

import dev.scaraz.mars.common.query.AuditableSpec;
import dev.scaraz.mars.user.datasource.domain.User;
import dev.scaraz.mars.user.datasource.domain.User_;
import dev.scaraz.mars.user.datasource.embedded.UserInfo_;
import dev.scaraz.mars.user.web.criteria.UserCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class UserSpecBuilder extends AuditableSpec<User, UserCriteria> {

    @Autowired
    private RoleSpecBuilder roleSpecBuilder;

    @Override
    public Specification<User> createSpec(UserCriteria criteria) {
        Specification<User> spec = Specification.where(null);
        if (criteria != null) {
            spec = nonNull(spec, criteria.getId(), path(User_.id));
            spec = nonNull(spec, criteria.getNik(), path(User_.nik));
            spec = nonNull(spec, criteria.getName(), path(User_.name));
            spec = nonNull(spec, criteria.getEmail(), path(User_.email));
            spec = nonNull(spec, criteria.getPhone(), path(User_.phone));

            spec = nonNull(spec, criteria.getWitel(), path(User_.info, UserInfo_.witel));
            spec = nonNull(spec, criteria.getTgId(), path(User_.info, UserInfo_.tgId));
            spec = nonNull(spec, criteria.getTgUsername(), path(User_.info, UserInfo_.tgUsername));

            spec = roleSpecBuilder.createSpec(spec, User_.authorities, criteria.getRole());
        }

        return auditSpec(spec, criteria);
    }
}

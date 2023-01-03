package dev.scaraz.mars.core.query.spec;

import dev.scaraz.mars.common.query.AuditableSpec;
import dev.scaraz.mars.core.domain.credential.Group_;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.domain.credential.UserCredential_;
import dev.scaraz.mars.core.domain.credential.User_;
import dev.scaraz.mars.core.query.criteria.GroupCriteria;
import dev.scaraz.mars.core.query.criteria.UserCredentialCriteria;
import dev.scaraz.mars.core.query.criteria.UserCriteria;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class UserSpecBuilder extends AuditableSpec<User, UserCriteria> {

    @Override
    public Specification<User> createSpec(UserCriteria criteria) {
        Specification<User> spec = Specification.where(null);
        if (criteria != null) {
            spec = nonNull(spec, criteria.getId(), User_.id);
            spec = nonNull(spec, criteria.getNik(), User_.nik);
            spec = nonNull(spec, criteria.getName(), User_.name);
            spec = nonNull(spec, criteria.getPhone(), User_.phone);
            spec = nonNull(spec, criteria.getTelegramId(), User_.telegramId);
            spec = nonNull(spec, criteria.getActive(), User_.active);

            if (criteria.getGroup() != null) {
                GroupCriteria group = criteria.getGroup();
                spec = nonNull(spec, group.getId(), User_.group, Group_.id);
                spec = nonNull(spec, group.getName(), User_.group, Group_.name);
            }

            if (criteria.getCredential() != null) {
                UserCredentialCriteria cr = criteria.getCredential();
                spec = nonNull(spec, cr.getId(), User_.credential, UserCredential_.id);
                spec = nonNull(spec, cr.getUsername(), User_.credential, UserCredential_.username);
            }
        }
        return auditSpec(spec, criteria);
    }

}

package dev.scaraz.mars.core.query.spec;

import dev.scaraz.mars.common.query.AuditableSpec;
import dev.scaraz.mars.core.domain.credential.*;
import dev.scaraz.mars.core.query.criteria.GroupCriteria;
import dev.scaraz.mars.core.query.criteria.RoleCriteria;
import dev.scaraz.mars.core.query.criteria.UserCredentialCriteria;
import dev.scaraz.mars.core.query.criteria.UserCriteria;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserSpecBuilder extends AuditableSpec<User, UserCriteria> {

    @Override
    public Specification<User> createSpec(UserCriteria criteria) {
        Specification<User> spec = Specification.where(null);
        if (criteria != null) {
            log.debug("User Criteria {}", criteria);
            spec = nonNull(spec, criteria.getId(), User_.id);
            spec = nonNull(spec, criteria.getNik(), User_.nik);
            spec = nonNull(spec, criteria.getName(), User_.name);
            spec = nonNull(spec, criteria.getPhone(), User_.phone);
            spec = nonNull(spec, criteria.getTelegramId(), User_.telegramId);
            spec = nonNull(spec, criteria.getActive(), User_.active);

            if (criteria.getRoles() != null) {
                RoleCriteria roles = criteria.getRoles();
                spec = nonNull(spec, roles.getId(), User_.roles, Role_.id);
                spec = nonNull(spec, roles.getName(), User_.roles, Role_.name);
            }

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

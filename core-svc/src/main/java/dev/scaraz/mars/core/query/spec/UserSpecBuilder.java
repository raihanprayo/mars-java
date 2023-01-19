package dev.scaraz.mars.core.query.spec;

import dev.scaraz.mars.common.query.AuditableSpec;
import dev.scaraz.mars.core.domain.credential.*;
import dev.scaraz.mars.core.query.criteria.GroupCriteria;
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
        Specification<User> spec = Specification.where(null);
        if (criteria != null) {
            log.debug("User Criteria {}", criteria);
            spec = nonNull(spec, criteria.getId(), User_.id);
            spec = nonNull(spec, criteria.getNik(), User_.nik);
            spec = nonNull(spec, criteria.getName(), User_.name);
            spec = nonNull(spec, criteria.getPhone(), User_.phone);
            spec = nonNull(spec, criteria.getEmail(), User_.email);
            spec = nonNull(spec, criteria.getWitel(), User_.witel);
            spec = nonNull(spec, criteria.getSto(), User_.sto);
            spec = nonNull(spec, criteria.getActive(), User_.active);

            if (criteria.getTg() != null) {
                UserTgCriteria tg = criteria.getTg();
                spec = nonNull(spec, tg.getId(), User_.tg, UserTg_.id);
                spec = nonNull(spec, tg.getUsername(), User_.tg, UserTg_.username);
            }

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

        }
        return auditSpec(spec, criteria);
    }

}

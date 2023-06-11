package dev.scaraz.mars.user.query;

import dev.scaraz.mars.user.domain.db.Role;
import dev.scaraz.mars.user.web.criteria.RoleCriteria;

import java.util.Optional;

public interface RoleQueryService extends BaseQueryService<Role, RoleCriteria> {
    Role findByName(String name);

    Optional<Role> findByNameOpt(String name);
}

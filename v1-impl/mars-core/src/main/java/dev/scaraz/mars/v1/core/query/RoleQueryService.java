package dev.scaraz.mars.v1.core.query;

import dev.scaraz.mars.v1.core.domain.credential.Role;
import dev.scaraz.mars.v1.core.query.criteria.RoleCriteria;

public interface RoleQueryService extends BaseQueryService<Role, RoleCriteria> {
    Role findByIdOrName(String idOrName);

}

package dev.scaraz.mars.core.v2.service.credential;

import dev.scaraz.mars.core.v2.domain.credential.Role;

public interface RoleService {
    Role save(Role role);

    Role getOrCreate(String name);
}

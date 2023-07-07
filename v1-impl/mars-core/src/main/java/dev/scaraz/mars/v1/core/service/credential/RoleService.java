package dev.scaraz.mars.v1.core.service.credential;

import dev.scaraz.mars.v1.core.domain.credential.Role;
import dev.scaraz.mars.v1.core.domain.credential.Roles;
import dev.scaraz.mars.v1.core.domain.credential.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RoleService {
    Role create(String name);

    @Transactional
    Role create(String name, int order);

    @Transactional
    List<Roles> addUserRoles(User user, Role... roles);
}

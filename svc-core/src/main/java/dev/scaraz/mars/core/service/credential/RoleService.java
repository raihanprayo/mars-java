package dev.scaraz.mars.core.service.credential;

import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.domain.credential.Role;
import dev.scaraz.mars.core.domain.credential.Roles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RoleService {
    Role create(String name);

    @Transactional
    Role create(String name, int order);

    @Transactional
    List<Roles> addUserRoles(Account account, Role... roles);
}

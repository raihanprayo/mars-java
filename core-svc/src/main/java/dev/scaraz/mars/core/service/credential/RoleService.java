package dev.scaraz.mars.core.service.credential;

import dev.scaraz.mars.core.domain.credential.Role;
import org.springframework.transaction.annotation.Transactional;

public interface RoleService {
    Role create(String name);

    @Transactional
    Role create(String name, long order);

    Role create(String name, String groupId);

    @Transactional
    Role create(String name, long order, String groupId);
}

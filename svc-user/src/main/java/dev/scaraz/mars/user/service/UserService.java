package dev.scaraz.mars.user.service;

import dev.scaraz.mars.user.domain.db.MarsUser;
import dev.scaraz.mars.user.web.dto.CreateUserDTO;
import dev.scaraz.mars.user.web.dto.UpdateRoleDTO;
import org.springframework.transaction.annotation.Transactional;

public interface UserService {
    MarsUser save(MarsUser user);

    MarsUser create(CreateUserDTO req);

    @Transactional
    MarsUser updateRole(String id, UpdateRoleDTO req);
}

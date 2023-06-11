package dev.scaraz.mars.user.service;

import dev.scaraz.mars.user.domain.db.Role;

import java.util.List;

public interface RoleService {
    Role save(Role role);

    List<Role> create(List<String> roleNames);
}

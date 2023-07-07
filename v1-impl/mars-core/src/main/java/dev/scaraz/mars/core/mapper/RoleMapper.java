package dev.scaraz.mars.core.mapper;

import dev.scaraz.mars.common.domain.response.RoleDTO;
import dev.scaraz.mars.core.domain.credential.Role;

public interface RoleMapper {
    RoleDTO toDTO(Role role);
}

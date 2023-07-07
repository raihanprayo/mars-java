package dev.scaraz.mars.v1.core.mapper;

import dev.scaraz.mars.common.domain.response.RoleDTO;
import dev.scaraz.mars.v1.core.domain.credential.Role;

public interface RoleMapper {
    RoleDTO toDTO(Role role);
}

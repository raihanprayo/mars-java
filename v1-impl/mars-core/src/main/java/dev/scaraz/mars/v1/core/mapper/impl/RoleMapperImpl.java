package dev.scaraz.mars.v1.core.mapper.impl;

import dev.scaraz.mars.common.domain.response.RoleDTO;
import dev.scaraz.mars.v1.core.domain.credential.Role;
import dev.scaraz.mars.v1.core.mapper.RoleMapper;
import org.springframework.stereotype.Component;

@Component
public class RoleMapperImpl implements RoleMapper {

    @Override
    public RoleDTO toDTO(Role role) {
        if (role == null) return null;
        return RoleDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .order(role.getOrder())
                .build();
    }

}

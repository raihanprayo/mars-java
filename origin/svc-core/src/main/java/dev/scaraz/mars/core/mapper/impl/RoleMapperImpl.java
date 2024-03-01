package dev.scaraz.mars.core.mapper.impl;

import dev.scaraz.mars.common.domain.response.RoleDTO;
import dev.scaraz.mars.core.domain.credential.Role;
import dev.scaraz.mars.core.mapper.RoleMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RoleMapperImpl implements RoleMapper {

    @Override
    public RoleDTO toDTO(Role role) {
        if (role == null) return null;
        return RoleDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }

}

package dev.scaraz.mars.core.mapper.impl;

import dev.scaraz.mars.common.domain.response.RoleDTO;
import dev.scaraz.mars.common.domain.response.RoleGroupDTO;
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
                .name(role.getName())
                .group(Optional.ofNullable(role.getGroup())
                        .map(g -> RoleGroupDTO.builder()
                                .id(g.getId())
                                .name(g.getName())
                                .build())
                        .orElse(null))
                .build();
    }

}

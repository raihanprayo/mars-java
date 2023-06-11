package dev.scaraz.mars.user.mapper;

import dev.scaraz.mars.user.domain.db.Role;
import dev.scaraz.mars.user.web.dto.RoleDTO;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface RoleMapper {

    RoleDTO toDTO(Role role);

    default String roleToString(Role role) {
        return role.getName();
    };

}

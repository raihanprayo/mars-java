package dev.scaraz.mars.user.mapper;

import dev.scaraz.mars.common.domain.general.RoleDTO;
import dev.scaraz.mars.common.domain.general.UserDTO;
import dev.scaraz.mars.common.domain.general.UserInfoDTO;
import dev.scaraz.mars.user.datasource.domain.Role;
import dev.scaraz.mars.user.datasource.domain.User;
import dev.scaraz.mars.user.datasource.embedded.UserInfo;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface CredentialMapper {

    UserDTO toDTO(User user);

    @Mapping(target = "sto", source = "sto.alias")
    UserInfoDTO toDTO(UserInfo info);

    User fromDTO(UserDTO user);

    @Mapping(target = "sto.alias", source = "sto")
    UserInfo fromDTO(UserInfoDTO info);


    RoleDTO toDTO(Role role);

    Role fromDTO(RoleDTO role);

}

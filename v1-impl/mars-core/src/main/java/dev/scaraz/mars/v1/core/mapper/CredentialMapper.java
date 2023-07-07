package dev.scaraz.mars.v1.core.mapper;

import dev.scaraz.mars.common.domain.response.GroupDTO;
import dev.scaraz.mars.common.domain.response.UserDTO;
import dev.scaraz.mars.common.domain.response.WhoamiDTO;
import dev.scaraz.mars.v1.core.domain.credential.Group;
import dev.scaraz.mars.v1.core.domain.credential.User;
import dev.scaraz.mars.v1.core.domain.credential.UserSetting;
import dev.scaraz.mars.v1.core.domain.credential.UserTg;

public interface CredentialMapper {
    UserDTO toDTO(User o);

    UserDTO.UserTgDTO toDTO(UserTg o);

    UserDTO.UserSettingDTO toDTO(UserSetting o);

    GroupDTO toPartialDTO(Group o);

    WhoamiDTO fromUser(User user);
}

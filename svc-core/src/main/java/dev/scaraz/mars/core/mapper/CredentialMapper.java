package dev.scaraz.mars.core.mapper;

import dev.scaraz.mars.common.domain.response.UserDTO;
import dev.scaraz.mars.common.domain.response.WhoamiDTO;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.domain.credential.UserSetting;
import dev.scaraz.mars.core.domain.credential.UserTg;

public interface CredentialMapper {
    UserDTO toDTO(User o);

    UserDTO.UserTgDTO toDTO(UserTg o);

    UserDTO.UserSettingDTO toDTO(UserSetting o);

    WhoamiDTO fromUser(User user);
}

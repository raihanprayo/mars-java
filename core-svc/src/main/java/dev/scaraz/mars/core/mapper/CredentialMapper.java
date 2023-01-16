package dev.scaraz.mars.core.mapper;

import dev.scaraz.mars.common.domain.response.GroupDTO;
import dev.scaraz.mars.common.domain.response.UserDTO;
import dev.scaraz.mars.common.domain.response.WhoamiDTO;
import dev.scaraz.mars.core.domain.credential.Group;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.domain.credential.UserCredential;
import dev.scaraz.mars.core.domain.credential.UserSetting;

public interface CredentialMapper {
    UserDTO toDTO(User o);

    UserDTO.UserCredentialDTO toDTO(UserCredential o);

    UserDTO.UserSettingDTO toDTO(UserSetting o);

    GroupDTO toPartialDTO(Group o);

    WhoamiDTO fromUser(User user);
}

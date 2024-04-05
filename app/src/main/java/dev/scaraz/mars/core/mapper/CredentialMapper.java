package dev.scaraz.mars.core.mapper;

import dev.scaraz.mars.common.domain.response.UserDTO;
import dev.scaraz.mars.common.domain.response.WhoamiDTO;
import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.domain.credential.AccountSetting;
import dev.scaraz.mars.core.domain.credential.AccountTg;

public interface CredentialMapper {
    UserDTO toDTO(Account o);

    UserDTO.UserTgDTO toDTO(AccountTg o);

    UserDTO.UserSettingDTO toDTO(AccountSetting o);

    WhoamiDTO fromUser(Account account);
}

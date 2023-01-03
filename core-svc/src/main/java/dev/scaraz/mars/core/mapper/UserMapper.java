package dev.scaraz.mars.core.mapper;

import dev.scaraz.mars.common.domain.response.WhoamiDTO;
import dev.scaraz.mars.core.domain.credential.User;

public interface UserMapper {
    WhoamiDTO fromUser(User user);
}

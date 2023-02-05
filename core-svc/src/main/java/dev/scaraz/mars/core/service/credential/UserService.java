package dev.scaraz.mars.core.service.credential;

import dev.scaraz.mars.common.domain.request.CreateUserDTO;
import dev.scaraz.mars.common.domain.request.TelegramCreateUserDTO;
import dev.scaraz.mars.common.domain.request.UpdateUserDashboardDTO;
import dev.scaraz.mars.core.domain.cache.BotRegistration;
import dev.scaraz.mars.core.domain.credential.Group;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.domain.credential.UserSetting;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;

public interface UserService extends UserDetailsPasswordService {

    User save(User user);

    UserSetting save(UserSetting credential);

    User create(CreateUserDTO user);

    void approval(String approvalId, boolean approved);

    void pairing(User user, BotRegistration registration);

    @Transactional
    void createFromBot(@Nullable Group group, boolean needApproval, TelegramCreateUserDTO req);

    User updatePartial(String userId, UpdateUserDashboardDTO dto);
}

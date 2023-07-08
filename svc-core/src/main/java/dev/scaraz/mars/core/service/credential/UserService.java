package dev.scaraz.mars.core.service.credential;

import dev.scaraz.mars.common.domain.request.CreateUserDTO;
import dev.scaraz.mars.common.domain.request.TelegramCreateUserDTO;
import dev.scaraz.mars.common.domain.request.UpdateUserDashboardDTO;
import dev.scaraz.mars.core.domain.cache.BotRegistration;
import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.domain.credential.AccountSetting;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.transaction.annotation.Transactional;

public interface UserService extends UserDetailsPasswordService {

    @Override
    Account updatePassword(UserDetails user, String newPassword);

    Account save(Account account);

    AccountSetting save(AccountSetting credential);

    Account create(CreateUserDTO user);

    void approval(String approvalId, boolean approved);

    void pairing(Account account, BotRegistration registration);

    @Transactional
    void createFromBot(boolean needApproval, TelegramCreateUserDTO req);

    Account updatePartial(String userId, UpdateUserDashboardDTO dto);
}

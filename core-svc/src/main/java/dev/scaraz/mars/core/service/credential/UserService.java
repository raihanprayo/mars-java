package dev.scaraz.mars.core.service.credential;

import dev.scaraz.mars.common.domain.request.TelegramCreateUserDTO;
import dev.scaraz.mars.core.domain.credential.Group;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.domain.credential.UserCredential;
import dev.scaraz.mars.core.util.DelegateUser;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

public interface UserService extends UserDetailsPasswordService {

    User save(User user);

    UserCredential save(UserCredential credential);

    @Transactional
    User createFromBot(Group group, TelegramCreateUserDTO req);
}

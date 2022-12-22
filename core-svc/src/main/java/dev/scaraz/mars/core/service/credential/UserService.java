package dev.scaraz.mars.core.service.credential;

import dev.scaraz.mars.common.domain.request.TelegramCreateUserDTO;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.util.DelegateUser;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService extends UserDetailsPasswordService {

    User createFromBot(TelegramCreateUserDTO req);
}

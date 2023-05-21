package dev.scaraz.mars.user.service.app;

import dev.scaraz.mars.common.domain.general.UserDTO;
import dev.scaraz.mars.user.datasource.domain.User;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService extends UserDetailsChecker, UserDetailsService {
    User save(User user);

    UserDTO save(UserDTO dto);
}

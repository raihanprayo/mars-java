package dev.scaraz.mars.core.query;

import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.query.criteria.UserCriteria;
import dev.scaraz.mars.core.util.DelegateUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface UserQueryService extends UserDetailsService {

    @Override
    DelegateUser loadUserByUsername(String username) throws UsernameNotFoundException;

    List<User> findAll();

    Page<User> findAll(Pageable pageable);

    List<User> findAll(UserCriteria criteria);

    Page<User> findAll(UserCriteria criteria, Pageable pageable);

    User findById(String id);

    User findByTelegramId(long tgId);
}

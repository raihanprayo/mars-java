package dev.scaraz.mars.v1.core.query;

import dev.scaraz.mars.v1.core.domain.credential.User;
import dev.scaraz.mars.v1.core.query.criteria.UserCriteria;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public interface UserQueryService extends UserDetailsService, BaseQueryService<User, UserCriteria> {

    @Override
    User loadUserByUsername(String username) throws UsernameNotFoundException;

    Optional<User> findOne(UserCriteria criteria);

    User findById(String id);

    Optional<User> findByIdOpt(String id);

    User findByTelegramId(long tgId);

    User findByNik(String nik);

    boolean existByNik(String nik);

    boolean existByCriteria(UserCriteria criteria);
}

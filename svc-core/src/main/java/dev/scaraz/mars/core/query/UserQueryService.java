package dev.scaraz.mars.core.query;

import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.query.criteria.UserCriteria;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

public interface UserQueryService extends UserDetailsService, BaseQueryService<Account, UserCriteria> {

    @Override
    Account loadUserByUsername(String username) throws UsernameNotFoundException;

    Optional<Account> findOne(UserCriteria criteria);

    Account findById(String id);

    Optional<Account> findByIdOpt(String id);

    Account findByTelegramId(long tgId);

    Account findByNik(String nik);

    boolean existByNik(String nik);

    boolean existByCriteria(UserCriteria criteria);
}

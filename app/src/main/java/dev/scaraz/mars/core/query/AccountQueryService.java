package dev.scaraz.mars.core.query;

import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.query.criteria.UserCriteria;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

public interface AccountQueryService extends UserDetailsService, BaseQueryService<Account, UserCriteria> {

    @Override
    Account loadUserByUsername(String username) throws UsernameNotFoundException;

    List<Account> findAll(UserCriteria criteria, Sort sort);

    Optional<Account> findOne(UserCriteria criteria);

    Account findById(String id);

    Account findByCurrentAccess();

    Optional<Account> findByIdOpt(String id);

    Account findByTelegramId(long tgId);

    Optional<Account> findByTelegramIdOpt(long tgId);

    Account findByNik(String nik);

    Account findByNikOrTelegramId(String nikOrTelegramId);

    boolean existByNik(String nik);

    boolean existByCriteria(UserCriteria criteria);
}

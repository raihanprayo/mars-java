package dev.scaraz.mars.core.query;

import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.query.criteria.AccountCriteria;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

public interface AccountQueryService extends UserDetailsService, BaseQueryService<Account, AccountCriteria> {

    @Override
    Account loadUserByUsername(String username) throws UsernameNotFoundException;

    List<Account> findAll(AccountCriteria criteria, Sort sort);

    Optional<Account> findOne(AccountCriteria criteria);

    Account findById(String id);

    Account findByCurrentAccess();

    Optional<Account> findByIdOpt(String id);

    Account findByTelegramId(long tgId);

    Optional<Account> findByTelegramIdOpt(long tgId);

    Account findByNik(String nik);

    Account findByNikOrTelegramId(String nikOrTelegramId);

    boolean existByNik(String nik);

    boolean existByCriteria(AccountCriteria criteria);
}

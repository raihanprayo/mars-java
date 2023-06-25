package dev.scaraz.mars.core.v2.repository.db.credential;

import dev.scaraz.mars.core.v2.domain.credential.Account;
import dev.scaraz.mars.core.v2.domain.credential.AccountRole;
import dev.scaraz.mars.core.v2.domain.credential.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Repository
public interface AccountRoleRepo extends JpaRepository<AccountRole, Long> {

    void deleteAllByAccountId(String userId);

    @Transactional
    default void save(Account account, Role... roles) {
        save(account, List.of(roles));
    }

    @Transactional
    default void save(Account account, Iterable<Role> roles) {
        saveAll(StreamSupport.stream(roles.spliterator(), false)
                .map(role -> AccountRole.builder()
                        .role(role)
                        .account(account)
                        .build())
                .collect(Collectors.toSet())
        );
    }
}

package dev.scaraz.mars.core.v2.repository.db.credential;

import dev.scaraz.mars.core.v2.domain.credential.Account;
import dev.scaraz.mars.core.v2.domain.credential.AccountRole;
import dev.scaraz.mars.core.v2.domain.credential.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.stream.Collectors;

@Repository
public interface AccountRoleRepo extends JpaRepository<AccountRole, Long> {

    void deleteAllByAccountId(String userId);

    @Transactional
    default void of(Account account, Role... roles) {
        saveAll(Arrays.stream(roles)
                .map(role -> AccountRole.builder()
                        .role(role)
                        .account(account)
                        .build())
                .collect(Collectors.toSet())
        );
    }
}

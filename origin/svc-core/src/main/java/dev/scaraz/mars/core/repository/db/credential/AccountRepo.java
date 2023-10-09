package dev.scaraz.mars.core.repository.db.credential;

import dev.scaraz.mars.core.domain.credential.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepo extends JpaRepository<Account, String>, JpaSpecificationExecutor<Account> {

    Optional<Account> findByNik(String nik);
    Optional<Account> findByEmailOrTgUsername(String username, String email);

    Optional<Account> findByTgId(long telegramId);

}

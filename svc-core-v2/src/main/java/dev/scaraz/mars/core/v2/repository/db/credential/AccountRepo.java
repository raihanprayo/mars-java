package dev.scaraz.mars.core.v2.repository.db.credential;

import dev.scaraz.mars.core.v2.domain.credential.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepo extends JpaRepository<Account, String> {

    List<Account> findAllByEnabledIsTrueAndExpiredActiveIsTrueAndExpiredDateLessThanEqual(
            Instant timestamp
    );

    boolean existsByUsernameIgnoreCase(String username);
    Optional<Account> findByUsernameIgnoreCase(String username);

}

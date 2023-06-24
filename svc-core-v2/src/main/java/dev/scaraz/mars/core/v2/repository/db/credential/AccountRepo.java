package dev.scaraz.mars.core.v2.repository.db.credential;

import dev.scaraz.mars.core.v2.domain.credential.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepo extends JpaRepository<Account, String> {
}

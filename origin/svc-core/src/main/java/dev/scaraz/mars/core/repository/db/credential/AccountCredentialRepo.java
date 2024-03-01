package dev.scaraz.mars.core.repository.db.credential;

import dev.scaraz.mars.core.domain.credential.AccountCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountCredentialRepo extends JpaRepository<AccountCredential, Long> {
}

package dev.scaraz.mars.core.repository.db.credential;

import dev.scaraz.mars.core.domain.credential.AccountEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountEventRepo extends JpaRepository<AccountEvent, String>, JpaSpecificationExecutor<AccountEvent> {
}

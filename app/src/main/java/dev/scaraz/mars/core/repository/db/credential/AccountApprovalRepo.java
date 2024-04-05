package dev.scaraz.mars.core.repository.db.credential;

import dev.scaraz.mars.core.domain.credential.AccountApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountApprovalRepo extends JpaRepository<AccountApproval, String>, JpaSpecificationExecutor<AccountApproval> {
    Optional<AccountApproval> findByIdOrNo(String id, String no);

    Optional<AccountApproval> findByTgId(long teelgramId);

    boolean existsByTgId(long teelgramId);

}

package dev.scaraz.mars.v1.core.repository.credential;

import dev.scaraz.mars.v1.core.domain.credential.UserApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserApprovalRepo extends JpaRepository<UserApproval, String> {
    Optional<UserApproval> findByIdOrNo(String id, String no);

    Optional<UserApproval> findByTgId(long teelgramId);

    boolean existsByTgId(long teelgramId);

}

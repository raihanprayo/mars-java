package dev.scaraz.mars.core.repository.credential;

import dev.scaraz.mars.core.domain.credential.UserApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserApprovalRepo extends JpaRepository<UserApproval, String> {
}

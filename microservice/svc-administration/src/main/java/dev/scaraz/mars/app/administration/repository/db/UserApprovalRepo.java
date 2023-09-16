package dev.scaraz.mars.app.administration.repository.db;

import dev.scaraz.mars.app.administration.domain.db.UserApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserApprovalRepo extends JpaRepository<UserApproval, String> {

    Optional<UserApproval> findByIdOrNo(String id, String no);

}

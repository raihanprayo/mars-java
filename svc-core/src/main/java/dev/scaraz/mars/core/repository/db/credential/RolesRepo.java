package dev.scaraz.mars.core.repository.db.credential;

import dev.scaraz.mars.core.domain.credential.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolesRepo extends JpaRepository<Roles, String> {
    List<Roles> findAllByUserId(String userId);

    boolean existsByUserIdAndRoleName(String userId, String role);

    void deleteByUserIdAndRoleIdIn(String userId, List<String> roleId);

}

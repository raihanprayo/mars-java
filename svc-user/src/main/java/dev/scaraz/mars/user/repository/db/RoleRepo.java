package dev.scaraz.mars.user.repository.db;

import dev.scaraz.mars.user.domain.db.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepo extends
        JpaRepository<Role, String>,
        JpaSpecificationExecutor<Role> {
    boolean existsByName(String roleName);
    Optional<Role> findByName(String roleName);
}

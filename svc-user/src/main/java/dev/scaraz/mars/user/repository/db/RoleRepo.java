package dev.scaraz.mars.user.repository.db;

import dev.scaraz.mars.user.domain.MarsUser;
import dev.scaraz.mars.user.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepo extends
        JpaRepository<Role, String>,
        JpaSpecificationExecutor<Role> {
    boolean existsByName(String roleName);
}

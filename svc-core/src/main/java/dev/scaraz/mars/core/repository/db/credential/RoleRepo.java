package dev.scaraz.mars.core.repository.db.credential;

import dev.scaraz.mars.core.domain.credential.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepo extends JpaRepository<Role, String>, JpaSpecificationExecutor<Role> {

    Optional<Role> findByName(String name);
    Optional<Role> findByIdOrName(String id, String name);

    boolean existsByName(String name);

}

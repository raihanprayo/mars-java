package dev.scaraz.mars.core.repository.credential;

import dev.scaraz.mars.core.domain.credential.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepo extends JpaRepository<Role, String> {
    Optional<Role> findByNameAndGroupIsNull(String name);
    Optional<Role> findByNameAndGroupId(String name, String groupId);

    boolean existsByNameAndGroupIsNull(String name);
    boolean existsByNameAndGroupId(String name, String groupId);
}

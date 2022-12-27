package dev.scaraz.mars.core.repository.credential;

import dev.scaraz.mars.core.domain.credential.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepo extends JpaRepository<Role, String>, JpaSpecificationExecutor<Role> {

    List<Role> findAllByGroupIsNull();
    List<Role> findAllByGroupIsNotNull();

    List<Role> findAllByGroupId(String groupId);

    Optional<Role> findByNameAndGroupIsNull(String name);
    Optional<Role> findByIdOrNameAndGroupIsNull(String id, String name);
    Optional<Role> findByNameAndGroupId(String name, String groupId);

    boolean existsByNameAndGroupIsNull(String name);
    boolean existsByNameAndGroupId(String name, String groupId);

}

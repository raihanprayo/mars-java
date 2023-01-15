package dev.scaraz.mars.core.repository.credential;

import dev.scaraz.mars.core.domain.credential.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupRepo extends JpaRepository<Group, String>, JpaSpecificationExecutor<Group> {
    Optional<Group> findByName(String name);
    Optional<Group> findByNameIgnoreCase(String name);

    Optional<Group> findByIdOrName(String id, String name);

}

package dev.scaraz.mars.user.datasource.repo;

import dev.scaraz.mars.user.datasource.domain.Role;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepo extends WithSpecRepository<Role, Long> {

    boolean existsByName(String name);

    Role findByName(String name);
}

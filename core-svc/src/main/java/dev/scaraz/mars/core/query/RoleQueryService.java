package dev.scaraz.mars.core.query;

import dev.scaraz.mars.core.domain.credential.Role;
import dev.scaraz.mars.core.query.criteria.RoleCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoleQueryService {
    Role findByIdOrName(String idOrName);

    Role findGroupRole(String groupId, String name);

    List<Role> findAll();

    Page<Role> findAll(Pageable pageable);

    List<Role> findAll(RoleCriteria criteria);

    Page<Role> findAll(RoleCriteria criteria, Pageable pageable);
}

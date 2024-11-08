package dev.scaraz.mars.core.query;

import dev.scaraz.mars.core.domain.credential.Role;
import dev.scaraz.mars.core.query.criteria.RoleCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoleQueryService extends BaseQueryService<Role, RoleCriteria> {
    Role findByIdOrName(String idOrName);

    List<Role> findAllByNames(List<String> roleNames);
}

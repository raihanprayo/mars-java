package dev.scaraz.mars.core.query.impl;

import dev.scaraz.mars.common.exception.web.NotFoundException;
import dev.scaraz.mars.common.utils.QueryBuilder;
import dev.scaraz.mars.core.domain.credential.Role;
import dev.scaraz.mars.core.query.RoleQueryService;
import dev.scaraz.mars.core.query.criteria.RoleCriteria;
import dev.scaraz.mars.core.repository.credential.RoleRepo;
import dev.scaraz.mars.core.repository.credential.RolesRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor

@Service
public class RoleQueryServiceImpl extends QueryBuilder implements RoleQueryService {

    private final RoleRepo roleRepo;

    @Override
    public Role findByIdOrName(String idOrName) {
        return roleRepo.findByIdOrNameAndGroupIsNull(idOrName, idOrName)
                .orElseThrow(() -> NotFoundException.entity(Role.class, "id/name", idOrName));
    }

    @Override
    public Role findGroupRole(String groupId, String name) {
        return roleRepo.findByNameAndGroupId(name, groupId)
                .orElseThrow(() -> NotFoundException.entity(Role.class, "group/name", String.format("%s/%s", groupId, name)));
    }

    @Override
    public List<Role> findAll() {
        return roleRepo.findAll();
    }

    @Override
    public Page<Role> findAll(Pageable pageable) {
        return roleRepo.findAll(pageable);
    }

    @Override
    public List<Role> findAll(RoleCriteria criteria) {
        return roleRepo.findAll(createSpecification(criteria));
    }

    @Override
    public Page<Role> findAll(RoleCriteria criteria, Pageable pageable) {
        return roleRepo.findAll(createSpecification(criteria), pageable);
    }

}

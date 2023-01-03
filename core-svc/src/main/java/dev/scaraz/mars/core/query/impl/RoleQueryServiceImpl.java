package dev.scaraz.mars.core.query.impl;

import dev.scaraz.mars.common.exception.web.NotFoundException;
import dev.scaraz.mars.core.domain.credential.Role;
import dev.scaraz.mars.core.query.RoleQueryService;
import dev.scaraz.mars.core.query.criteria.RoleCriteria;
import dev.scaraz.mars.core.query.spec.RoleSpecBuilder;
import dev.scaraz.mars.core.repository.credential.RoleRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor

@Service
public class RoleQueryServiceImpl implements RoleQueryService {

    private final RoleRepo repo;
    private final RoleSpecBuilder specBuilder;

    @Override
    public Role findByIdOrName(String idOrName) {
        return repo.findByIdOrNameAndGroupIsNull(idOrName, idOrName)
                .orElseThrow(() -> NotFoundException.entity(Role.class, "id/name", idOrName));
    }

    @Override
    public Role findGroupRole(String groupId, String name) {
        return repo.findByNameAndGroupId(name, groupId)
                .orElseThrow(() -> NotFoundException.entity(Role.class, "group/name", String.format("%s/%s", groupId, name)));
    }

    @Override
    public List<Role> findAll() {
        return repo.findAll();
    }

    @Override
    public Page<Role> findAll(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    public List<Role> findAll(RoleCriteria criteria) {
        return repo.findAll(specBuilder.createSpec(criteria));
    }

    @Override
    public Page<Role> findAll(RoleCriteria criteria, Pageable pageable) {
        return repo.findAll(specBuilder.createSpec(criteria), pageable);
    }

    @Override
    public long count() {
        return repo.count();
    }

    @Override
    public long count(RoleCriteria criteria) {
        return repo.count(specBuilder.createSpec(criteria));
    }
}

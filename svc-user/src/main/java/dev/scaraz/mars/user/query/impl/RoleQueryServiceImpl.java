package dev.scaraz.mars.user.query.impl;

import dev.scaraz.mars.common.exception.web.NotFoundException;
import dev.scaraz.mars.user.domain.db.Role;
import dev.scaraz.mars.user.query.RoleQueryService;
import dev.scaraz.mars.user.query.spec.RoleSpecBuilder;
import dev.scaraz.mars.user.repository.db.RoleRepo;
import dev.scaraz.mars.user.web.criteria.RoleCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleQueryServiceImpl implements RoleQueryService {

    private final RoleRepo repo;
    private final RoleSpecBuilder specBuilder;

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
    public Optional<Role> findByNameOpt(String name) {
        return repo.findByName(name);
    }

    @Override
    public Role findByName(String name) {
        return findByNameOpt(name).orElseThrow(() -> NotFoundException.entity(Role.class, "name", name));
    }


}

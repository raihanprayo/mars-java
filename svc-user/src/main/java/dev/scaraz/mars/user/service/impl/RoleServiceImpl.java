package dev.scaraz.mars.user.service.impl;

import dev.scaraz.mars.user.domain.Role;
import dev.scaraz.mars.user.repository.db.RoleRepo;
import dev.scaraz.mars.user.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepo repo;

    @Override
    public Role save(Role role) {
        return repo.save(role);
    }

}

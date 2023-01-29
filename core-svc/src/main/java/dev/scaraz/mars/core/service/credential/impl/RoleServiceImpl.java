package dev.scaraz.mars.core.service.credential.impl;

import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.core.domain.credential.Role;
import dev.scaraz.mars.core.domain.credential.Roles;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.repository.credential.GroupRepo;
import dev.scaraz.mars.core.repository.credential.RoleRepo;
import dev.scaraz.mars.core.repository.credential.RolesRepo;
import dev.scaraz.mars.core.service.credential.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepo repo;
    private final RolesRepo rolesRepo;
    private final GroupRepo groupRepo;

    public Role save(Role role) {
        return repo.save(role);
    }

    @Override
    public Role create(String name) {
        return create(name, 0);
    }

    @Override
    @Transactional
    public Role create(String name, long order) {
        if (repo.existsByName(name))
            throw BadRequestException.duplicateEntity(Role.class, "name", name);

        log.info("CREATE NEW ROLE {}", name);
        return save(Role.builder()
                .name(name.toLowerCase())
                .order(0)
                .build());
    }

    @Override
    @Transactional
    public List<Roles> addUserRoles(User user, Role... roles) {
        List<Roles> mappedRole = Stream.of(roles)
                .map(role -> Roles.builder()
                        .user(user)
                        .role(role)
                        .build())
                .collect(Collectors.toList());

        log.info("ADD ROLE FOR USER {} -- {}", user.getId(), roles);
        return rolesRepo.saveAll(mappedRole);
    }

}

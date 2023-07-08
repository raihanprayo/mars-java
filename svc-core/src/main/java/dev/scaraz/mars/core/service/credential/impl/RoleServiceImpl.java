package dev.scaraz.mars.core.service.credential.impl;

import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.core.domain.credential.Role;
import dev.scaraz.mars.core.domain.credential.Roles;
import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.repository.db.credential.RoleRepo;
import dev.scaraz.mars.core.repository.db.credential.RolesRepo;
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

    public Role save(Role role) {
        return repo.save(role);
    }

    @Override
    public Role create(String name) {
        return create(name, 0);
    }

    @Override
    @Transactional
    public Role create(String name, int order) {
        if (repo.existsByName(name))
            throw BadRequestException.duplicateEntity(Role.class, "name", name);

        log.info("CREATE NEW ROLE {}", name);
        return save(Role.builder()
                .name(name.toLowerCase())
                .build());
    }

    @Override
    @Transactional
    public List<Roles> addUserRoles(Account account, Role... roles) {
        List<Roles> mappedRole = Stream.of(roles)
                .map(role -> Roles.builder()
                        .account(account)
                        .role(role)
                        .build())
                .collect(Collectors.toList());

        log.info("ADD ROLE FOR USER {} -- {}", account.getId(), roles);
        return rolesRepo.saveAll(mappedRole);
    }

}

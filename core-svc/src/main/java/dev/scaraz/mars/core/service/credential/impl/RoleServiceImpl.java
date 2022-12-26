package dev.scaraz.mars.core.service.credential.impl;

import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.exception.web.NotFoundException;
import dev.scaraz.mars.core.domain.credential.Group;
import dev.scaraz.mars.core.domain.credential.Role;
import dev.scaraz.mars.core.repository.credential.GroupRepo;
import dev.scaraz.mars.core.repository.credential.RoleRepo;
import dev.scaraz.mars.core.service.credential.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepo repo;
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
        if (repo.existsByNameAndGroupIsNull(name))
            throw BadRequestException.duplicateEntity(Role.class, "name", name);

        log.info("CREATE NEW ROLE {}", name);
        return save(Role.builder()
                .name(name.toLowerCase())
                .order(0)
                .build());
    }

    @Override
    public Role createGroupRole(String name, String groupId) {
        return createGroupRole(name, 0, groupId, false);
    }

    @Override
    @Transactional
    public Role createGroupRole(String name, long order, String groupId, boolean defaultRole) {
        Group group = groupRepo.findById(groupId)
                .orElseThrow(() -> NotFoundException.entity(Group.class, "id", groupId));

        log.info("CREATE NEW ROLE {} FOR GROUP {}", name, group.getName());

        if (repo.existsByNameAndGroupId(name, groupId))
            throw BadRequestException.duplicateEntity(Role.class, "name", name);

        Role role = save(Role.builder()
                .name(name.toLowerCase())
                .order(order)
                .group(group)
                .build());

        if (defaultRole) {
            if (group.getSetting().getDefaultRole() != null)
                throw BadRequestException.args("Group cannot have more than 1 default role");
            else
                group.getSetting().setDefaultRole(role);
        }

        groupRepo.save(group);
        return role;
    }

}

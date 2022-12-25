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
    public Role create(String name, String groupId) {
        return create(name, 0, groupId);
    }

    @Override
    @Transactional
    public Role create(String name, long order, String groupId) {
        Group group = groupRepo.findById(groupId)
                .orElseThrow(() -> NotFoundException.entity(Group.class, "id", groupId));

        if (repo.existsByNameAndGroupId(name, groupId))
            throw BadRequestException.duplicateEntity(Role.class, "name", name);

        log.info("CREATE NEW ROLE {} FOR GROUP {}", name, group.getName());
        return save(Role.builder()
                .name(name.toLowerCase())
                .order(order)
                .group(group)
                .build());
    }

}

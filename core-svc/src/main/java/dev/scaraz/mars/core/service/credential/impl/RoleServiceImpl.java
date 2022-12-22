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

import java.util.Optional;

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
        Optional<Role> roleOpt = repo.findByNameAndGroupIsNull(name);
        if (roleOpt.isPresent())
            throw new BadRequestException("role.duplicate.exist");

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
                .orElseThrow(() -> new NotFoundException("entity.not.found.detail", (Object) "Group", "id", groupId));

        Optional<Role> roleOpt = repo.findByNameAndGroupId(name, groupId);
        if (roleOpt.isPresent())
            throw new BadRequestException("role.duplicate.exist");

        log.info("CREATE NEW ROLE {} FOR GROUP {}", name, group.getName());
        return save(Role.builder()
                .name(name.toLowerCase())
                .order(order)
                .group(group)
                .build());
    }

}

package dev.scaraz.mars.core.service.credential.impl;

import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.core.domain.credential.*;
import dev.scaraz.mars.core.repository.credential.GroupMemberRepo;
import dev.scaraz.mars.core.repository.credential.GroupRepo;
import dev.scaraz.mars.core.repository.credential.RoleRepo;
import dev.scaraz.mars.core.service.credential.GroupService;
import dev.scaraz.mars.core.service.credential.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;

@Slf4j
@RequiredArgsConstructor

@Service
public class GroupServiceImpl implements GroupService {

    private final GroupRepo repo;
    private final GroupMemberRepo groupMemberRepo;

    private final RoleRepo roleRepo;
    private final RoleService roleService;

    @Override
    @Transactional
    public Group create(String name, boolean canLogin) {
        boolean duplicateExist = repo.findByName(name).isPresent();
        if (duplicateExist)
            throw BadRequestException.duplicateEntity(Group.class, "name", name);

        log.info("CREATE NEW GROUP WITH NAME {}", name);
        Group group = repo.save(Group.builder()
                .name(name)
                .setting(GroupSetting.builder()
                        .canLogin(canLogin)
                        .build())
                .build());

        group.addRoles(
                roleService.createGroupRole("supervisor", 2, group.getId(), false),
                roleService.createGroupRole("member", 1, group.getId(), true)
        );
        return group;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Group addUser(Group group, User user, @Nullable Role groupRole) {
        if (groupRole == null) groupRole = group.getSetting().getDefaultRole();

        if (groupRole == null)
            throw BadRequestException.args("Try to provide user role in group");

        GroupMember member = GroupMember.builder()
                .user(user)
                .group(group)
                .role(groupRole)
                .build();

        groupMemberRepo.save(member);

        log.info("ADD USER {} TO GROUP {} WITH ROLE {}", user.getName(), group.getName(), groupRole.getName());
        return group;
    }

    public Group removeUser(Group group, User user) {
        groupMemberRepo.deleteByUserIdAndGroupId(user.getId(), group.getId());
        group.getMembers().remove(user);
        user.setGroup(null);
        return group;
    }

}

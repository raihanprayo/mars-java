package dev.scaraz.mars.core.service.credential.impl;

import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.core.domain.credential.Group;
import dev.scaraz.mars.core.domain.credential.GroupSetting;
import dev.scaraz.mars.core.repository.credential.GroupRepo;
import dev.scaraz.mars.core.service.credential.GroupService;
import dev.scaraz.mars.core.service.credential.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor

@Service
public class GroupServiceImpl implements GroupService {

    private final GroupRepo repo;
    private final RoleService roleService;

    @Override
    @Transactional
    public Group create(String name, boolean canLogin) {
        boolean duplicateExist = repo.findByName(name).isPresent();
        if (duplicateExist)
            throw BadRequestException.duplicateEntity(Group.class, "name", name);

        log.info("CREATE NEW GROUP WITH NAME {}", name);
        return repo.save(Group.builder()
                .name(name)
                .setting(GroupSetting.builder()
                        .canLogin(canLogin)
                        .build())
                .build());
    }

}

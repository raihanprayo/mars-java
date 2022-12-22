package dev.scaraz.mars.core.service;

import dev.scaraz.mars.core.domain.credential.Group;
import dev.scaraz.mars.core.domain.credential.GroupSetting;
import dev.scaraz.mars.core.domain.credential.Role;
import dev.scaraz.mars.core.repository.credential.GroupRepo;
import dev.scaraz.mars.core.repository.credential.RoleRepo;
import dev.scaraz.mars.core.service.credential.GroupService;
import dev.scaraz.mars.core.service.credential.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InitializerService {

    private final GroupRepo groupRepo;
    private final GroupService groupService;

    private final RoleRepo roleRepo;
    private final RoleService roleService;

    @Async
    @Transactional
    public void preInitRoles() {
        Map<String, Integer> names = Map.of("admin", 100, "user", 1);
        for (String name : names.keySet()) {
            Optional<Role> roleOpt = roleRepo.findByNameAndGroupIsNull(name);
            if (roleOpt.isEmpty()) roleService.create(name, names.get(name));
        }
    }

    @Async
    @Transactional
    public void preInitGroups() {
        Map<String, Boolean> names = Map.of(
                "ROC Assurance", true,
                "ROC TIAL", true,
                "Banten", false,
                "Bekasi", false,
                "Bogor", false,
                "Jakbar", false,
                "Jakpus", false,
                "Jaktim", false,
                "Jakut", false,
                "Tangerang", false);

        for (String name : names.keySet()) {
            boolean groupExist = groupRepo.findByName(name).isPresent();
            if (!groupExist) groupService.create(name, names.get(name));
        }
    }

}

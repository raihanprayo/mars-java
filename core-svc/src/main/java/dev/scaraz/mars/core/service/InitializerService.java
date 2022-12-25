package dev.scaraz.mars.core.service;

import dev.scaraz.mars.common.tools.enums.Product;
import dev.scaraz.mars.core.domain.credential.Role;
import dev.scaraz.mars.core.repository.credential.GroupRepo;
import dev.scaraz.mars.core.repository.credential.RoleRepo;
import dev.scaraz.mars.core.repository.order.IssueRepo;
import dev.scaraz.mars.core.service.credential.GroupService;
import dev.scaraz.mars.core.service.credential.RoleService;
import dev.scaraz.mars.core.service.order.IssueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private final IssueRepo issueRepo;
    private final IssueService issueService;

    @Async
    @Transactional
    public void preInitRoles() {
        Map<String, Integer> names = Map.of(
                "admin", 100,
                "user", 1);
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

    @Async
    @Transactional
    public void preInitIssue() {
        Map<String, Product> names = Map.of("lambat", Product.INTERNET,
                "intermittent", Product.INTERNET,
                "tbb", Product.INTERNET,
                "blank", Product.IPTV,
                "login", Product.IPTV,
                "network", Product.IPTV,
                "matot", Product.VOICE,
                "bulk", Product.VOICE,
                "icog", Product.VOICE);

        for (String name : names.keySet()) {
            if (issueRepo.existsByNameAndProduct(name, names.get(name))) continue;
            issueService.create(name, names.get(name), null);
        }
    }

}

package dev.scaraz.mars.core.v2.service.credential.impl;

import dev.scaraz.mars.common.config.properties.MarsProperties;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.core.v2.domain.credential.Account;
import dev.scaraz.mars.core.v2.domain.credential.Role;
import dev.scaraz.mars.core.v2.repository.db.credential.AccountRepo;
import dev.scaraz.mars.core.v2.repository.db.credential.AccountRoleRepo;
import dev.scaraz.mars.core.v2.service.credential.AccountService;
import dev.scaraz.mars.core.v2.service.credential.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final MarsProperties marsProperties;

    private final AccountRepo repo;
    private final AccountRoleRepo roleRepo;

    private final RoleService roleService;

    @PostConstruct
    @Transactional()
    public void init() {
        List<String> roles = List.of(
                AppConstants.Authority.ADMIN_ROLE,
                AppConstants.Authority.AGENT_ROLE,
                AppConstants.Authority.USER_ROLE);

        Role adminRole = null;
        for (String roleName : roles) {
            Role role = roleService.getOrCreate(roleName);
            if (AppConstants.Authority.ADMIN_ROLE.equals(roleName))
                adminRole = role;
        }

        if (adminRole != null) {
            Account account = save(Account.builder()
                    .name("Administrator")
                    .username("admin")
                    .email("admin@dummy.com")
                    .witel(marsProperties.getWitel())
                    .build());

            roleRepo.of(account, adminRole);
        }
    }

    @Override
    public Account save(Account a) {
        return repo.save(a);
    }

}

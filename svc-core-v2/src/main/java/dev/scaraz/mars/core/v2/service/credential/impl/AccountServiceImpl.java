package dev.scaraz.mars.core.v2.service.credential.impl;

import dev.scaraz.mars.common.config.properties.MarsProperties;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.core.v2.config.security.CorePasswordEncoder;
import dev.scaraz.mars.core.v2.domain.app.Config;
import dev.scaraz.mars.core.v2.domain.credential.Account;
import dev.scaraz.mars.core.v2.domain.credential.AccountCredential;
import dev.scaraz.mars.core.v2.domain.credential.AccountExpired;
import dev.scaraz.mars.core.v2.domain.embed.AccountMisc;
import dev.scaraz.mars.core.v2.domain.credential.Role;
import dev.scaraz.mars.core.v2.repository.db.credential.AccountRepo;
import dev.scaraz.mars.core.v2.repository.db.credential.AccountRoleRepo;
import dev.scaraz.mars.core.v2.service.app.ConfigService;
import dev.scaraz.mars.core.v2.service.credential.AccountService;
import dev.scaraz.mars.core.v2.service.credential.RoleService;
import dev.scaraz.mars.core.v2.util.ConfigConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final MarsProperties marsProperties;
    private final ConfigService configService;
    private final CorePasswordEncoder passwordEncoder;

    private final AccountRepo repo;
    private final AccountRoleRepo accountRoleRepo;

    private final RoleService roleService;

    @PostConstruct
    @Transactional
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
            if (repo.existsByUsernameIgnoreCase("admin")) return;
            log.info("CREATE NEW ADMINISTRATOR ACCOUNT");

            Config passwordAlgo = configService.get(ConfigConstants.CRD_DEFAULT_PASSWORD_ALGO_STR);
            Config passwordSecret = configService.get(ConfigConstants.CRD_DEFAULT_PASSWORD_SECRET_STR);
            Config passwordHashIteration = configService.get(ConfigConstants.CRD_DEFAULT_PASSWORD_ITERATION_INT);

            Account account = save(Account.builder()
                    .name("Administrator")
                    .username("admin")
                    .misc(AccountMisc.builder()
                            .email("admin@dummy.com")
                            .witel(marsProperties.getWitel())
                            .build())
                    .build());

            AccountExpired expired = AccountExpired.inactive();
            AccountCredential credential = AccountCredential.builder()
                    .account(account)
                    .algorithm(passwordAlgo.getValue())
                    .secret(passwordSecret.getValue())
                    .hashIteration(passwordHashIteration.getAsInt())
                    .password("admin")
                    .priority(10)
                    .build();

            expired.setAccount(account);
            credential.setPassword(passwordEncoder.encode(credential));

            account.setExpired(expired);
            account.setCredentials(Set.of(credential));
            accountRoleRepo.save(save(account), adminRole);
        }
    }

    @Override
    public Account save(Account a) {
        return repo.save(a);
    }

}

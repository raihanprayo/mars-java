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
import dev.scaraz.mars.core.v2.repository.db.credential.AccountCredentialRepo;
import dev.scaraz.mars.core.v2.repository.db.credential.AccountExpiredRepo;
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
    private final AccountCredentialRepo credentialRepo;
    private final AccountExpiredRepo expiredRepo;
    private final AccountRoleRepo accountRoleRepo;

    private final RoleService roleService;

    @Override
    public Account save(Account a) {
        return repo.save(a);
    }

}

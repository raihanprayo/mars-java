package dev.scaraz.mars.core.v2.service.credential.impl;

import dev.scaraz.mars.common.config.properties.MarsProperties;
import dev.scaraz.mars.core.v2.domain.credential.Account;
import dev.scaraz.mars.core.v2.domain.credential.AccountCredential;
import dev.scaraz.mars.core.v2.domain.credential.AccountExpired;
import dev.scaraz.mars.core.v2.repository.db.credential.AccountCredentialRepo;
import dev.scaraz.mars.core.v2.repository.db.credential.AccountExpiredRepo;
import dev.scaraz.mars.core.v2.repository.db.credential.AccountRepo;
import dev.scaraz.mars.core.v2.repository.db.credential.AccountRoleRepo;
import dev.scaraz.mars.core.v2.service.app.ConfigService;
import dev.scaraz.mars.core.v2.service.credential.AccountService;
import dev.scaraz.mars.core.v2.service.credential.RoleService;
import dev.scaraz.mars.security.MarsPasswordEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final MarsProperties marsProperties;
    private final MarsPasswordEncoder passwordEncoder;
    private final ConfigService configService;

    private final AccountRepo repo;
    private final AccountCredentialRepo credentialRepo;
    private final AccountExpiredRepo expiredRepo;
    private final AccountRoleRepo accountRoleRepo;

    private final RoleService roleService;

    @Override
    public Account save(Account a) {
        return repo.save(a);
    }

    @Override
    public AccountCredential save(AccountCredential a) {
        return credentialRepo.save(a);
    }

    @Override
    public AccountExpired save(AccountExpired a) {
        return expiredRepo.save(a);
    }

}

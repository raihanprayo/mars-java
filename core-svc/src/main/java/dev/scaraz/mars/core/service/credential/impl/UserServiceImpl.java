package dev.scaraz.mars.core.service.credential.impl;

import dev.scaraz.mars.common.domain.request.TelegramCreateUserDTO;
import dev.scaraz.mars.common.exception.web.NotFoundException;
import dev.scaraz.mars.core.config.datasource.AuditProvider;
import dev.scaraz.mars.core.domain.credential.*;
import dev.scaraz.mars.core.repository.credential.*;
import dev.scaraz.mars.core.service.credential.GroupService;
import dev.scaraz.mars.core.service.credential.UserService;
import dev.scaraz.mars.core.util.DelegateUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@RequiredArgsConstructor

@Service
public class UserServiceImpl implements UserService {

    private final AuditProvider auditProvider;
    private final UserRepo userRepo;
    private final UserCredentialRepo credentialRepo;
    private final UserSettingRepo settingRepo;
    private final RoleRepo roleRepo;
    private final RolesRepo rolesRepo;

    private final GroupRepo groupRepo;

    @Lazy
    private final GroupService groupService;

    private final PasswordEncoder passwordEncoder;

    @Override
    public User save(User user) {
        return userRepo.save(user);
    }

    @Override
    public UserCredential save(UserCredential credential) {
        return credentialRepo.save(credential);
    }

    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        User account = ((DelegateUser) user).getUser();
        account.getCredential().setPassword(passwordEncoder.encode(newPassword));
        credentialRepo.save(account.getCredential());
        return user;
    }

    @Override
    @Transactional
    public User createFromBot(Group group, TelegramCreateUserDTO req) {
        auditProvider.setName(req.getName());
        try {
            return userRepo.saveAndFlush(User.builder()
                    .nik(req.getNik())
                    .name(req.getName())
                    .phone(req.getPhone())
                    .telegramId(req.getTelegramId())
                    .build());
        }
        finally {
            auditProvider.clear();
        }
    }

}

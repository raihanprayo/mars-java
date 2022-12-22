package dev.scaraz.mars.core.service.credential.impl;

import dev.scaraz.mars.common.domain.request.TelegramCreateUserDTO;
import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.exception.web.NotFoundException;
import dev.scaraz.mars.core.config.datasource.AuditProvider;
import dev.scaraz.mars.core.domain.credential.Group;
import dev.scaraz.mars.core.domain.credential.Role;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.repository.credential.*;
import dev.scaraz.mars.core.service.credential.UserService;
import dev.scaraz.mars.core.util.DelegateUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor

@Service
public class UserServiceImpl implements UserService {

    private final AuditProvider auditProvider;
    private final UserRepo userRepo;
    private final UserCredentialRepo credentialRepo;
    private final RoleRepo roleRepo;
    private final GroupRepo groupRepo;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        User account = ((DelegateUser) user).getUser();
        account.getCredential().setPassword(passwordEncoder.encode(newPassword));
        credentialRepo.save(account.getCredential());
        return user;
    }

    @Override
    public User createFromBot(TelegramCreateUserDTO req) {
        Group group = groupRepo.findByName(req.getGroupName())
                .orElseThrow(() -> NotFoundException.entity(Group.class, "name", req.getGroupName()));

        Role role = roleRepo.findByNameAndGroupIsNull("user")
                .orElseThrow(() -> NotFoundException.entity(Role.class, "name", "user"));

        User user = User.builder()
                .nik(req.getNik())
                .name(req.getName())
                .phone(req.getPhone())
                .telegramId(req.getTelegramId())
                .group(group)
                .build();

        user.addRole(role);
        try {
            auditProvider.setName(user.getName());
            return userRepo.save(user);
        }
        finally {
            auditProvider.clear();
        }
    }

}

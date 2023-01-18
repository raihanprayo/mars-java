package dev.scaraz.mars.core.service.credential.impl;

import dev.scaraz.mars.common.domain.request.CreateUserDTO;
import dev.scaraz.mars.common.domain.request.TelegramCreateUserDTO;
import dev.scaraz.mars.common.domain.request.UserUpdateDashboardDTO;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.core.config.datasource.AuditProvider;
import dev.scaraz.mars.core.domain.credential.*;
import dev.scaraz.mars.core.query.GroupQueryService;
import dev.scaraz.mars.core.query.RoleQueryService;
import dev.scaraz.mars.core.query.UserQueryService;
import dev.scaraz.mars.core.query.criteria.RoleCriteria;
import dev.scaraz.mars.core.repository.credential.*;
import dev.scaraz.mars.core.service.credential.RoleService;
import dev.scaraz.mars.core.service.credential.UserService;
import dev.scaraz.mars.core.util.DelegateUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor

@Service
public class UserServiceImpl implements UserService {

    private final AuditProvider auditProvider;
    private final UserRepo userRepo;
    private final UserQueryService userQueryService;
    private final UserCredentialRepo credentialRepo;
    private final UserSettingRepo settingRepo;

    private final RoleQueryService roleQueryService;
    private final RoleService roleService;

    private final GroupQueryService groupQueryService;

    private final PasswordEncoder passwordEncoder;

    private final RolesRepo rolesRepo;

    @Override
    public User save(User user) {
        return userRepo.save(user);
    }

    @Override
    public UserCredential save(UserCredential credential) {
        return credentialRepo.save(credential);
    }

    @Override
    public UserSetting save(UserSetting credential) {
        return settingRepo.save(credential);
    }

    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        User account;
        if (user instanceof User) account = (User) user;
        else {
            account = userQueryService.findById(((DelegateUser) user).getId());
        }

        account.getCredential().setPassword(passwordEncoder.encode(newPassword));
        credentialRepo.save(account.getCredential());
        return user;
    }

    @Override
    @Transactional
    public User create(CreateUserDTO req) {
        User nuser = save(User.builder()
                .name(req.getName())
                .nik(req.getNik())
                .phone(req.getPhone())
                .active(Optional.ofNullable(req.getActive()).orElse(true))
                .credential(UserCredential.builder()
                        .email(req.getEmail())
                        .username(req.getUsername())
                        .build())
                .build());

        if (req.getRoles().size() > 0) {
            List<Role> roles = roleQueryService.findAll(RoleCriteria.builder()
                    .id(new StringFilter().setIn(req.getRoles()))
                    .build());
            roleService.addUserRoles(nuser, roles.toArray(new Role[0]));
        }
        else {
            Role roleUser = roleQueryService.findByIdOrName("user");
            roleService.addUserRoles(nuser, roleUser);
        }

        if (req.getGroup() != null) {
            Group group = groupQueryService.findByIdOrName(req.getGroup());
            nuser.setGroup(group);
        }

        return save(nuser);
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
                    .group(group)
                    .active(true)
                    .build());
        }
        finally {
            auditProvider.clear();
        }
    }

    @Override
    @Transactional
    public User updatePartial(String userId, UserUpdateDashboardDTO dto) {
        log.info("PARTIAL DATA USER UPDATE {}", dto);
        User user = userQueryService.findById(userId);

        if (dto.getNik() != null) user.setNik(dto.getNik());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getActive() != null) user.setActive(dto.getActive());

        if (dto.getRoles() != null) {
            List<String> removedIds = dto.getRoles().getRemoved();
            List<String> selectedIds = dto.getRoles().getSelected();

            rolesRepo.deleteByUserIdAndRoleIdIn(userId, removedIds);

            Set<Role> roles = new HashSet<>();
            for (String roleId : selectedIds) {
                Role role = roleQueryService.findByIdOrName(roleId);
                if (rolesRepo.existsByUserIdAndRoleName(userId, role.getName())) {
                    roles.add(role);
                    continue;
                }

                rolesRepo.save(Roles.builder()
                        .user(user)
                        .role(role)
                        .build());
                roles.add(role);
            }

            user.setRoles(roles);
        }

        return save(user);
    }

}

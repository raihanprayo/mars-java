package dev.scaraz.mars.user.initializer;

import dev.scaraz.mars.common.config.properties.MarsProperties;
import dev.scaraz.mars.common.tools.enums.Witel;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.user.domain.db.MarsUser;
import dev.scaraz.mars.user.domain.db.Role;
import dev.scaraz.mars.user.domain.db.Roles;
import dev.scaraz.mars.user.repository.db.RoleRepo;
import dev.scaraz.mars.user.repository.db.RolesRepo;
import dev.scaraz.mars.user.service.ScriptService;
import dev.scaraz.mars.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InitialRoleScript {

    private static final String SCRIPT = "initial-user-roles";

    private final RoleRepo roleRepo;
    private final RolesRepo rolesRepo;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final ScriptService scriptService;
    private final MarsProperties marsProperties;

    @Autowired
    @Transactional
    public void exec() {
        if (scriptService.isExecuted(SCRIPT)) return;
        List<String> roles = List.of(AppConstants.Authority.ADMIN_ROLE,
                AppConstants.Authority.USER_ROLE,
                AppConstants.Authority.AGENT_ROLE
        );

        Role adminRole = null;
        for (String roleName : roles) {
            if (roleRepo.existsByName(roleName)) continue;
            Role role = roleRepo.saveAndFlush(Role.builder()
                    .name(roleName)
                    .build());

            if (roleName.equals(AppConstants.Authority.ADMIN_ROLE)) adminRole = role;
        }

        if (adminRole == null) return;

        MarsUser admin = userService.save(MarsUser.builder()
                .name("administrator")
                .nik("admin")
                .enabled(true)
                .witel(marsProperties.getWitel())
                .password(passwordEncoder.encode("admin"))
                .build());


        rolesRepo.save(Roles.of(adminRole, admin));
        scriptService.updateAsExecuted(SCRIPT);
    }

}

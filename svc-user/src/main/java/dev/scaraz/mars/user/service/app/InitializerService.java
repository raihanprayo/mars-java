package dev.scaraz.mars.user.service.app;

import dev.scaraz.mars.common.config.properties.MarsProperties;
import dev.scaraz.mars.common.tools.enums.Witel;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.user.datasource.domain.Role;
import dev.scaraz.mars.user.datasource.domain.Sto;
import dev.scaraz.mars.user.datasource.domain.User;
import dev.scaraz.mars.user.datasource.embedded.UserInfo;
import dev.scaraz.mars.user.datasource.repo.RoleRepo;
import dev.scaraz.mars.user.datasource.repo.StoRepo;
import dev.scaraz.mars.user.datasource.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Slf4j
@RequiredArgsConstructor

@Service
public class InitializerService {
    private final MarsProperties marsProperties;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepo roleRepo;
    private final UserRepo userRepo;
    private final StoRepo stoRepo;

    @Transactional
    public void createInitialRoles() {
        Map<String, Integer> roles = Map.of(
                AppConstants.Authority.ADMIN_ROLE, 1,
                AppConstants.Authority.USER_ROLE, 100);

        for (String role : roles.keySet()) {
            if (roleRepo.existsByName(role)) continue;

            int order = roles.get(role);
            log.info("CREATE INITIAL ROLE {} ORDER {}", role, order);
            roleRepo.save(Role.builder()
                    .name(role)
                    .order(order)
                    .build());
        }
    }

    @Transactional
    public void createInitialAccount() {
        if (userRepo.existsByNik("admin")) return;
        log.info("CREATE INITIAL ADMIN ACCOUNT");

        Role role = roleRepo.findByName(AppConstants.Authority.ADMIN_ROLE);
        User user = User.builder()
                .nik("admin")
                .name("administrator")
                .phone("000000000000")
                .enabled(true)
                .email("administrator@mars.com")
                .authorities(new HashSet<>(Set.of(role)))
                .password(passwordEncoder.encode("admin"))
                .info(UserInfo.builder()
                        .witel(marsProperties.getWitel())
                        .build())
                .build();

        userRepo.save(user);
    }

    public void initialSTO() {
        try {
            InputStream contentStream = getClass().getResourceAsStream("/list-sto.csv");
            String[] lines = new String(contentStream.readAllBytes(), StandardCharsets.UTF_8)
                    .split("\n");

            for (String line : lines) {
                String[] values = line.split(";");

                try {
                    Witel witel = Witel.valueOf(values[0]);
                    String datel = values[1];
                    String alias = values[2];
                    String name = values[3].replace("\r", "");

                    if (stoRepo.existsByAliasIgnoreCase(alias)) continue;

                    stoRepo.save(Sto.builder()
                            .witel(witel)
                            .datel(datel)
                            .alias(alias)
                            .name(name)
                            .build());
                }
                catch (Exception ex) {

                }
            }
        }
        catch (Exception ex) {

        }
    }

}

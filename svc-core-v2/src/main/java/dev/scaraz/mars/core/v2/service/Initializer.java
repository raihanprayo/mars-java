package dev.scaraz.mars.core.v2.service;

import dev.scaraz.mars.common.config.properties.MarsProperties;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.core.v2.domain.app.Config;
import dev.scaraz.mars.core.v2.domain.app.Sto;
import dev.scaraz.mars.core.v2.domain.credential.Account;
import dev.scaraz.mars.core.v2.domain.credential.AccountCredential;
import dev.scaraz.mars.core.v2.domain.credential.AccountExpired;
import dev.scaraz.mars.core.v2.domain.credential.Role;
import dev.scaraz.mars.core.v2.domain.csv.StoCsvValue;
import dev.scaraz.mars.core.v2.domain.embed.AccountMisc;
import dev.scaraz.mars.core.v2.repository.db.app.StoRepo;
import dev.scaraz.mars.core.v2.repository.db.credential.AccountCredentialRepo;
import dev.scaraz.mars.core.v2.repository.db.credential.AccountExpiredRepo;
import dev.scaraz.mars.core.v2.repository.db.credential.AccountRepo;
import dev.scaraz.mars.core.v2.repository.db.credential.AccountRoleRepo;
import dev.scaraz.mars.core.v2.service.app.ConfigService;
import dev.scaraz.mars.core.v2.service.app.StoCsvService;
import dev.scaraz.mars.core.v2.service.app.StoService;
import dev.scaraz.mars.core.v2.service.credential.AccountService;
import dev.scaraz.mars.core.v2.service.credential.RoleService;
import dev.scaraz.mars.core.v2.util.ConfigConstants;
import dev.scaraz.mars.security.MarsPasswordEncoder;
import io.github.avew.CsvewResultReader;
import io.github.avew.CsvewValidationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Random;

import static dev.scaraz.mars.core.v2.util.ConfigConstants.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class Initializer {

    private final MarsProperties marsProperties;
    private final MarsPasswordEncoder passwordEncoder;

    private final AccountRepo accountRepo;
    private final AccountCredentialRepo accountCredentialRepo;
    private final AccountExpiredRepo accountExpiredRepo;
    private final AccountRoleRepo accountRoleRepo;
    private final AccountService accountService;

    private final ConfigService configService;

    private final RoleService roleService;

    private final StoRepo stoRepo;
    private final StoService stoService;
    private final StoCsvService stoCsvService;


    @Transactional
    public void createApplicationConfigs() {
        configService.bulkCreate(Tag.APPLICATION,
                new ConfigEntry<>(APP_CONFIRMATION_DRT, Duration.ofMinutes(30)),
                new ConfigEntry<>(APP_PENDING_CONFIRMATION_DRT, Duration.ofHours(1)),
                new ConfigEntry<>(APP_ALLOW_AGENT_CREATE_TICKET_BOOL, false),
                new ConfigEntry<>(APP_USER_REGISTRATION_APPROVAL_BOOL, true)
        );

        configService.bulkCreate(Tag.ACCOUNT,
                new ConfigEntry<>(ACC_EXPIRED_BOOL, true),
                new ConfigEntry<>(ACC_EXPIRED_DRT, Duration.ofDays(365))
        );

        configService.bulkCreate(Tag.CREDENTIAL,
                new ConfigEntry<>(CRD_DEFAULT_PASSWORD_ALGO_STR, "bcrypt"),
                new ConfigEntry<>(CRD_DEFAULT_PASSWORD_ITERATION_INT, 24_200),
                new ConfigEntry<>(CRD_DEFAULT_PASSWORD_SECRET_STR, () -> {
                    Random random = new Random();
                    byte[] randomSecret = new byte[16];
                    random.nextBytes(randomSecret);
                    return Base64.getEncoder().encodeToString(randomSecret);
                })
        );

        configService.bulkCreate(Tag.JWT,
                new ConfigEntry<>(JWT_TOKEN_EXPIRED_DRT, Duration.ofHours(2)),
                new ConfigEntry<>(JWT_TOKEN_REFRESH_EXPIRED_DRT, Duration.ofHours(12))
        );

        configService.bulkCreate(Tag.TELEGRAM,
                new ConfigEntry<>(TG_START_CMD_ISSUE_COLUMN_INT, 3)
        );
    }

    @Transactional
    public void createDefaultsStoFromCsv() throws IOException {
        try (InputStream is = getClass().getResourceAsStream("/list-sto.csv")) {
            CsvewResultReader<StoCsvValue> result = stoCsvService.process(is);
            if (result.isError()) {
                for (CsvewValidationDTO validation : result.getValidations()) {
                    log.warn("Failed to create STO entries: {}", validation.getMessage());
                }
                return;
            }

            for (StoCsvValue value : result.getValues()) {
                if (stoRepo.existsById(value.getCode())) continue;

                log.info("CREATE NEW STO ({}) for Witel {}", value.getCode(), value.getWitel());
                stoRepo.save(Sto.builder()
                        .id(value.getCode())
                        .name(value.getName())
                        .witel(value.getWitel())
                        .datel(value.getDatel())
                        .build());
            }
        }
    }

    @Transactional
    public void createRolesAndAdministrator() {
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
            if (accountRepo.existsByUsernameIgnoreCase("admin")) return;
            log.info("CREATE NEW ADMINISTRATOR ACCOUNT");

            Config passwordAlgo = configService.get(ConfigConstants.CRD_DEFAULT_PASSWORD_ALGO_STR);
            Config passwordSecret = configService.get(ConfigConstants.CRD_DEFAULT_PASSWORD_SECRET_STR);

            Account account = accountService.save(Account.builder()
                    .name("Administrator")
                    .username("admin")
                    .enabled(true)
                    .misc(AccountMisc.builder()
                            .email("admin@dummy.com")
                            .witel(marsProperties.getWitel())
                            .build())
                    .build());
            accountRoleRepo.save(account, adminRole);

            AccountExpired expired = AccountExpired.inactive();
            expired.setAccount(account);
            accountExpiredRepo.save(expired);

            AccountCredential credential = AccountCredential.builder()
                    .account(account)
                    .algorithm(passwordAlgo.getValue())
                    .secret(passwordSecret.getValue())
                    .password("admin")
                    .priority(10)
                    .build();
            credential.setPassword(passwordEncoder.encode(credential));
            accountCredentialRepo.save(credential);
        }
    }

}

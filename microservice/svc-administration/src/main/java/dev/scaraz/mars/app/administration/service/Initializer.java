package dev.scaraz.mars.app.administration.service;

import dev.scaraz.mars.app.administration.domain.db.Config;
import dev.scaraz.mars.app.administration.service.app.ConfigService;
import dev.scaraz.mars.common.utils.ConfigConstants;
import dev.scaraz.mars.common.utils.ConfigEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class Initializer implements CommandLineRunner {

    private final ConfigService configService;
    private final RealmService realmService;

    public void createConfig() {
        configService.bulkCreate(ConfigConstants.Tag.APPLICATION,
                new ConfigEntry<>(Config.USER_REGISTRATION_APPROVAL_BOOL, true, "registrasi diperlukan persetujuan"),
                new ConfigEntry<>(Config.USER_REGISTRATION_APPROVAL_DRT, Duration.ofDays(1), "lama waktu persetujuan registrasi"),
                new ConfigEntry<>(Config.USER_REGISTRATION_EMAIL_LIST, new ArrayList<>(), "list email yang akan ditampilkan untuk registrasi")
        );
        configService.bulkCreate(ConfigConstants.Tag.TELEGRAM,
                new ConfigEntry<>(Config.TG_CMD_ISSUE_COLUMN_INT, 3, "jumlah kolom issue perbaris pada command /start")
        );
    }

    @Override
    public void run(String... args) throws Exception {
        createConfig();
        realmService.createAdministration();
        realmService.createWitelClients();
        realmService.createClientScopeDetails();
    }
}

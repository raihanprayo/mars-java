package dev.scaraz.mars.app.administration.service;

import dev.scaraz.mars.app.administration.domain.db.Config;
import dev.scaraz.mars.app.administration.service.app.ConfigService;
import dev.scaraz.mars.common.utils.ConfigEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class Initializer {

    private final ConfigService configService;

    public void createConfig() {
        configService.bulkCreate(
                new ConfigEntry<>(Config.USER_REGISTRATION_APPROVAL_BOOL, true, "registrasi diperlukan persetujuan"),
                new ConfigEntry<>(Config.USER_REGISTRATION_APPROVAL_DRT, Duration.ofDays(1), "lama waktu persetujuan registrasi"),
                new ConfigEntry<>(Config.USER_REGISTRATION_EMAIL_LIST, new ArrayList<>(), "list email yang akan ditampilkan untuk registrasi")
        );
    }

}

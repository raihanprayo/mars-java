package dev.scaraz.mars.user.service;

import dev.scaraz.mars.user.domain.db.AppConfig;
import dev.scaraz.mars.user.domain.db.AppConfigCategory;

import javax.annotation.Nullable;
import java.time.Duration;

public interface AppConfigService {

    AppConfig save(AppConfig o);

    AppConfigCategory save(AppConfigCategory o);

    AppConfigCategory getCategory(String name);

    AppConfig create(String name, Object value, @Nullable String description);

    AppConfig create(String name, String value, @Nullable String description);

    <T extends Number> AppConfig create(String name, T value, @Nullable String description);

    AppConfig create(String name, Boolean value, @Nullable String description);

    AppConfig create(String name, Iterable<String> values, @Nullable String description);

    AppConfig create(String name, Duration duration, @Nullable String description);

    interface JWT {
        String CATEGORY = "jwt";
        String PREFIX = CATEGORY + "-prefix";
        String EXPIRED_DURATION = CATEGORY + "-duration";
        String REFRESH_EXPIRED_DURATION = CATEGORY + "-refresh-duration";
    }
}

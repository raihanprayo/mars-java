package dev.scaraz.mars.user.service.impl;

import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.tools.Translator;
import dev.scaraz.mars.user.domain.db.AppConfig;
import dev.scaraz.mars.user.domain.db.AppConfigCategory;
import dev.scaraz.mars.user.repository.db.AppConfigCategoryRepo;
import dev.scaraz.mars.user.repository.db.AppConfigRepo;
import dev.scaraz.mars.user.service.AppConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.Duration;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor

@Service
public class AppConfigServiceImpl implements AppConfigService {

    private final AppConfigRepo repo;
    private final AppConfigCategoryRepo categoryRepo;

    @Override
    public AppConfig save(AppConfig o) {
        return repo.save(o);
    }

    @Override
    public AppConfigCategory save(AppConfigCategory o) {
        return categoryRepo.save(o);
    }

    @Override
    public AppConfigCategory getCategory(String name) {
        return categoryRepo.findById(name)
                .orElseGet(() -> categoryRepo.save(AppConfigCategory.builder()
                        .id(name)
                        .build()));
    }

    @Override
    public AppConfig create(String name, Object value, @Nullable String description) {
        if (value instanceof String) return create(name, (String) value, description);
        if (value instanceof Number) return create(name, (Number) value, description);
        if (value instanceof Boolean) return create(name, (Boolean) value, description);
        if (value instanceof Iterable) return create(name, (Iterable<String>) value, description);
        if (value instanceof Duration) return create(name, (Duration) value, description);

        if (value instanceof Serializable) {
            AppConfig appConfig = createNew(name, description);
            appConfig.setAsJson((Serializable) value);
            return appConfig;
        }

        throw BadRequestException.args("Unable to determine config type");
    }

    @Override
    public AppConfig create(String name, String value, @Nullable String description) {
        AppConfig appConfig = createNew(name, description);
        appConfig.setAsString(value);
        return appConfig;
    }

    @Override
    public <T extends Number> AppConfig create(String name, T value, @Nullable String description) {
        AppConfig appConfig = createNew(name, description);
        appConfig.setAsNumber(value);
        return appConfig;
    }

    @Override
    public AppConfig create(String name, Boolean value, @Nullable String description) {
        AppConfig appConfig = createNew(name, description);
        appConfig.setAsBoolean(value);
        return appConfig;
    }

    @Override
    public AppConfig create(String name, Iterable<String> values, @Nullable String description) {
        AppConfig appConfig = createNew(name, description);
        appConfig.setAsArray(values);
        return appConfig;
    }

    @Override
    public AppConfig create(String name, Duration duration, @Nullable String description) {
        AppConfig appConfig = createNew(name, description);
        appConfig.setAsDuration(duration);
        return appConfig;
    }

    private AppConfig createNew(String name, @Nullable String description) {
        AppConfig appConfig = new AppConfig();

        String titleCode = "app.config.key." + name;
        String title = Translator.tr(titleCode);
        if (Objects.equals(title, titleCode)) title = name;

        appConfig.setName(name);
        appConfig.setTitle(title);
        appConfig.setDescription(description);
        return appConfig;
    }

}

package dev.scaraz.mars.core.service;

import dev.scaraz.mars.core.domain.AppConfig;

public interface AppConfigService {
    AppConfig save(AppConfig config);

    AppConfig getById(long id);

    AppConfig getCloseConfirm();

    AppConfig getAllowLogin();
}

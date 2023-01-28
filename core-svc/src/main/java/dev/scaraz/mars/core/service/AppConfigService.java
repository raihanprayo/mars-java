package dev.scaraz.mars.core.service;

import dev.scaraz.mars.core.domain.AppConfig;

public interface AppConfigService {
    AppConfig save(AppConfig config);

    AppConfig getById(long id);

    AppConfig getCloseConfirm_int();

    AppConfig getAllowLogin_bool();

    AppConfig getRegistrationRequireApproval_bool();

    AppConfig getSendRegistrationApproval_bool();

    AppConfig getPostPending_int();

    AppConfig getApprovalDurationHour_int();
}

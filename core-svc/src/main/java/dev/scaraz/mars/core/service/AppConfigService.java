package dev.scaraz.mars.core.service;

import dev.scaraz.mars.core.domain.AppConfig;

import javax.annotation.Nullable;
import java.time.Duration;

public interface AppConfigService {
    AppConfig save(AppConfig config);

    AppConfig create(long id, String name, Object value, @Nullable String description);

    AppConfig create(long id, String name, String value, @Nullable String description);

    <T extends Number> AppConfig create(long id, String name, T value, @Nullable String description);

    AppConfig create(long id, String name, Boolean value, @Nullable String description);

    AppConfig create(long id, String name, Iterable<String> values, @Nullable String description);

    AppConfig create(long id, String name, Duration duration, @Nullable String description);

    AppConfig getById(long id);

    AppConfig getCloseConfirm_int();

    AppConfig getAllowLogin_bool();

    AppConfig getRegistrationRequireApproval_bool();

    AppConfig getSendRegistrationApproval_bool();

    AppConfig getPostPending_int();

    AppConfig getApprovalDurationHour_int();

    AppConfig getApprovalAdminEmails_arr();
}

package dev.scaraz.mars.core.service.app;

import dev.scaraz.mars.core.datasource.domain.AppConfig;

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

    AppConfig getCloseConfirm_drt();

    AppConfig getAllowLogin_bool();

    AppConfig getRegistrationRequireApproval_bool();

    AppConfig getSendRegistrationApproval_bool();

    AppConfig getPostPending_drt();

    AppConfig getApprovalDurationHour_drt();

    AppConfig getApprovalAdminEmails_arr();

    AppConfig getAllowAgentCreateTicket_bool();
}

package dev.scaraz.mars.common.utils;

import lombok.Getter;

import java.util.function.Supplier;

public final class ConfigConstants {
    private ConfigConstants() {
    }

    public static final String
            APP_ALLOW_AGENT_CREATE_TICKET_BOOL = "agent-allowed-to-create-ticket",
            APP_USER_REGISTRATION_APPROVAL_BOOL = "user-registration-approval",
            APP_USER_REGISTRATION_APPROVAL_DRT = "user-registration-approval-duration",
            APP_ISSUE_GAUL_EXCLUDE_LIST = "issue-gaul-binding-exclusion",
            APP_SOLUTION_REPORT_EXCLUDE_LIST = "solution-report-exclusion";

    public static final String
            ACC_EXPIRED_BOOL = "account-expireable",
            ACC_EXPIRED_DRT = "account-expired-duration",
            ACC_REGISTRATION_EMAILS_LIST = "account-registration-approval-email";

    public static final String
            CRD_PASSWORD_ALGO_STR = "password-algo",
            CRD_PASSWORD_SECRET_STR = "password-secret",
            CRD_PASSWORD_HASH_ITERATION_INT = "password-hash-iteration",
            CRD_PASSWORD_HISTORY_INT = "password-history";

    public static final String
            JWT_TOKEN_REFRESH_EXPIRED_DRT = "token-refresh-expired",
            JWT_TOKEN_EXPIRED_DRT = "token-expired";

    public static final String
            TG_CONFIRMATION_DRT = "confirmation-duration",
            TG_PENDING_CONFIRMATION_DRT = "confirmation-pending-duration",
            TG_START_CMD_ISSUE_COLUMN_INT = "tg-stat-command-issue-col-count";

    public interface Tag {
        String APPLICATION = "app";
        String ACCOUNT = "account";
        String CREDENTIAL = "credential";
        String JWT = "jwt";
        String TELEGRAM = "telegram";
    }

    @Getter
    public static class ConfigEntry<T> {
        private final String key;
        private final Supplier<T> value;
        private final String description;

        public ConfigEntry(String key, Supplier<T> value, String description) {
            this.key = key;
            this.value = value;
            this.description = description;
        }

        public ConfigEntry(String key, Supplier<T> value) {
            this(key, value, null);
        }

        public ConfigEntry(String key, T value, String description) {
            this(key, () -> value, description);
        }

        public ConfigEntry(String key, T value) {
            this(key, () -> value);
        }
    }
}

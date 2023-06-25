package dev.scaraz.mars.core.v2.util;

import lombok.Getter;

import java.util.function.Supplier;

public final class ConfigConstants {
    private ConfigConstants() {
    }

    public static final String
            APP_ALLOW_AGENT_CREATE_TICKET_BOOL = "agent-allowed-to-create-ticket",
            APP_USER_REGISTRATION_APPROVAL_BOOL = "user-registration-approval",
            APP_CONFIRMATION_DRT = "confirmation-duration",
            APP_PENDING_CONFIRMATION_DRT = "confirmation-pending-duration";
    public static final String
            ACC_EXPIRED_BOOL = "account-expireable",
            ACC_EXPIRED_DRT = "account-expired-duration";
    public static final String
            CRD_DEFAULT_PASSWORD_ALGO_STR = "password-algo",
            CRD_DEFAULT_PASSWORD_SECRET_STR = "password-secret",
            CRD_DEFAULT_PASSWORD_ITERATION_INT = "password-hash-iteration";

    public static final String
            JWT_TOKEN_REFRESH_EXPIRED_DRT = "token-refresh-expired",
            JWT_TOKEN_EXPIRED_DRT = "token-expired";

    public static final String
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

        public ConfigEntry(String key, Supplier<T> value) {
            this.key = key;
            this.value = value;
        }

        public ConfigEntry(String key, T value) {
            this(key, () -> value);
        }
    }
}

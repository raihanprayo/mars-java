package dev.scaraz.mars.core.v2.util;

import lombok.Getter;

import java.time.Duration;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class ConfigConstants {
    private ConfigConstants() {
    }

    public static final Map<String, Tupple<?>> DEFAULTS;
    public static final String
            ALLOW_AGENT_CREATE_TICKET_BOOL = "agent-allowed-to-create-ticket",
            USER_REGISTRATION_APPROVAL_BOOL = "user-registration-approval",
            CONFIRMATION_DRT = "confirmation-duration",
            PENDING_CONFIRMATION_DRT = "confirmation-pending-duration";
    public static final String
            TOKEN_EXPIRED_DRT = "token-expired",
            TOKEN_EXPIRED_UNIT = "token-expired-unit",
            TOKEN_REFRESH_EXPIRED_DRT = "token-refresh-expired",
            TOKEN_REFRESH_EXPIRED_UNIT = "token-refresh-expired-unit",
            ACCOUNT_EXPIRED_DRT = "account-expired";
    public static final String
            TG_START_CMD_ISSUE_COLUMN_COUNT = "tg-stat-command-issue-col-count";

    public interface Tag {
        String APPLICATION = "app";
        String CREDENTIAL = "credential";
        String TELEGRAM = "telegram";
    }

    @Getter
    public static class Tupple<T> {
        private final String tag;
        private final T value;
        private final String description;

        public Tupple(String tag, T value, String description) {
            this.tag = tag;
            this.value = value;
            this.description = description;
        }

        public Tupple(String tag, T value) {
            this(tag, value, null);
        }

        static <T> Tupple<T> with(T value, String description) {
            return new Tupple<>(null, value, description);
        }

        static <T> Tupple<T> with(T value) {
            return with(value, null);
        }

        static <T> Tupple<T> withApp(T value, String description) {
            return new Tupple<>(Tag.APPLICATION, value, description);
        }

        static <T> Tupple<T> withApp(T value) {
            return withApp(value, null);
        }

        static <T> Tupple<T> withCredential(T value, String description) {
            return new Tupple<>(Tag.CREDENTIAL, value, description);
        }

        static <T> Tupple<T> withCredential(T value) {
            return withCredential(value, null);
        }

        static <T> Tupple<T> withTelegram(T value, String description) {
            return new Tupple<>(Tag.TELEGRAM, value, description);
        }

        static <T> Tupple<T> withTelegram(T value) {
            return withTelegram(value, null);
        }
    }

    static {
        Map<String, Tupple<?>> map = new LinkedHashMap<>();

        map.put(ALLOW_AGENT_CREATE_TICKET_BOOL, Tupple.withApp(false));
        map.put(USER_REGISTRATION_APPROVAL_BOOL, Tupple.withApp(true));
        map.put(CONFIRMATION_DRT, Tupple.withApp(Duration.ofMinutes(30)));
        map.put(PENDING_CONFIRMATION_DRT, Tupple.withApp(Duration.ofHours(1)));

        map.put(TOKEN_EXPIRED_DRT, Tupple.withCredential(Duration.ofHours(2)));
        map.put(TOKEN_REFRESH_EXPIRED_DRT, Tupple.withCredential(Duration.ofHours(6)));
        map.put(ACCOUNT_EXPIRED_DRT, Tupple.withCredential(Duration.ofDays(730)));

        map.put(TG_START_CMD_ISSUE_COLUMN_COUNT, Tupple.withTelegram(3));
        DEFAULTS = Collections.unmodifiableMap(map);
    }
}

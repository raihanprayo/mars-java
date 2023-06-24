package dev.scaraz.mars.core.v2.config.datasource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;

public final class DatasourceAuditor implements AuditorAware<String>, DateTimeProvider {

    private static final ThreadLocal<String> usernameAttr = new InheritableThreadLocal<>();
    private static final ThreadLocal<Instant> timestampAttr = new InheritableThreadLocal<>();

    @Override
    public Optional<TemporalAccessor> getNow() {
        if (timestampAttr.get() != null)
            return Optional.of(timestampAttr.get());

        return Optional.of(Instant.now().atZone(ZoneId.of("Asia/Jakarta")));
    }

    @Override
    public Optional<String> getCurrentAuditor() {
        if (StringUtils.isNoneBlank(usernameAttr.get()))
            return Optional.of(usernameAttr.get());

        return Optional.of("system");
    }

    public static void setUsername(String username) {
        usernameAttr.set(username);
    }

    public static void setTimestamp(Instant timestamp) {
        timestampAttr.set(timestamp);
    }

}
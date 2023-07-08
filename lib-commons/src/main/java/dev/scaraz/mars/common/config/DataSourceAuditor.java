package dev.scaraz.mars.common.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DataSourceAuditor implements AuditorAware<String>, DateTimeProvider {
    public static final String BEAN_NAME = "datasource-auditor";
    private static final ThreadLocal<String> usernameAttr = new InheritableThreadLocal<>();
    private static final ThreadLocal<Instant> timestampAttr = new InheritableThreadLocal<>();


    private final List<AuditorResolver> resolvers = new ArrayList<>();

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

        for (AuditorResolver auditor : resolvers) {
            String result = auditor.get();
            if (StringUtils.isNoneBlank(result))
                return Optional.ofNullable(result);
        }

        return Optional.of("system");
    }

    public DataSourceAuditor addAuditorResolver(AuditorResolver resolver) {
        resolvers.add(resolver);
        return this;
    }

    public static void setUsername(String username) {
        usernameAttr.set(username);
    }

    public static void setTimestamp(Instant timestamp) {
        timestampAttr.set(timestamp);
    }

    public static void clear() {
        usernameAttr.remove();
        timestampAttr.remove();
    }

    @FunctionalInterface
    public interface AuditorResolver {

        @Nullable
        String get();

    }
}

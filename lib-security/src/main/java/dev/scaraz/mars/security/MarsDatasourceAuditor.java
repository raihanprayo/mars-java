package dev.scaraz.mars.security;

import dev.scaraz.mars.security.MarsAuthenticationToken;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.Nullable;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAccessor;
import java.util.Objects;
import java.util.Optional;

public class MarsDatasourceAuditor implements AuditorAware<String>, DateTimeProvider {

    private static final ThreadLocal<String> nameAttribute = new InheritableThreadLocal<>();
    private static final ThreadLocal<Instant> timestampAttribute = new InheritableThreadLocal<>();

    @Override
    public Optional<String> getCurrentAuditor() {
        String username = getUsername();

        if (username != null) return Optional.of(username);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof MarsAuthenticationToken) {
            MarsAuthenticationToken token = (MarsAuthenticationToken) authentication;
            return Optional.of(token.getPrincipal().getNik());
        }
        return Optional.of("system");
    }

    @Override
    public Optional<TemporalAccessor> getNow() {
        Instant instant = Objects.requireNonNullElse(getTimestamp(), Instant.now());
        return Optional.of(instant.atZone(ZoneOffset.of("+07")));
    }

    public void setUsername(String nik) {
        nameAttribute.set(nik);
    }

    @Nullable
    public String getUsername() {
        return nameAttribute.get();
    }

    public void setTimestamp(Instant timestamp) {
        timestampAttribute.set(timestamp);
    }

    @Nullable
    public Instant getTimestamp() {
        return timestampAttribute.get();
    }

}

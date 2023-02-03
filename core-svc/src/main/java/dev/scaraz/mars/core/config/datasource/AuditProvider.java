package dev.scaraz.mars.core.config.datasource;

import dev.scaraz.mars.core.config.security.CoreAuthenticationToken;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.util.DelegateUser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAccessor;
import java.util.Objects;
import java.util.Optional;

@Component("coreAuditorAware")
public class AuditProvider implements AuditorAware<String>, DateTimeProvider {

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private Instant timestamp;

    @Override
    public Optional<String> getCurrentAuditor() {
        if (name != null) return Optional.of(name);

        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof DelegateUser) return Optional.of(((DelegateUser) principal).getNik());
            else if (principal instanceof User) return Optional.of(((User) principal).getNik());
        }
        return Optional.of("system");
    }

    @Override
    public Optional<TemporalAccessor> getNow() {
        Instant instant = Objects.requireNonNullElse(timestamp, Instant.now());
        return Optional.of(instant.atZone(ZoneOffset.of("+07")));
    }

    public void clear() {
        name = null;
        timestamp = null;
    }
}

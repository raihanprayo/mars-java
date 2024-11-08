package dev.scaraz.mars.core.config.datasource;

import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.util.DelegateUser;
import dev.scaraz.mars.security.authentication.identity.MarsAuthentication;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import jakarta.annotation.Nullable;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAccessor;
import java.util.Objects;
import java.util.Optional;

@Component("coreAuditorAware")
public class AuditProvider implements AuditorAware<String>, DateTimeProvider {

    private static final ThreadLocal<String> nameAttribute = new InheritableThreadLocal<>();
    private static final ThreadLocal<Instant> timestampAttribute = new InheritableThreadLocal<>();

    @Override
    public Optional<String> getCurrentAuditor() {
        if (getName() != null) return Optional.of(getName());

        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof DelegateUser)
                return Optional.of(((DelegateUser) principal).getNik());
            else if (principal instanceof Account)
                return Optional.of(((Account) principal).getNik());
            else if (principal instanceof MarsAuthentication)
                return Optional.of(((MarsAuthentication) principal).getName());
        }
        return Optional.of("system");
    }

    @Override
    public Optional<TemporalAccessor> getNow() {
        Instant instant = Objects.requireNonNullElse(getTimestamp(), Instant.now());
        return Optional.of(instant.atZone(ZoneOffset.of("+07")));
    }

    public void clear() {
        nameAttribute.set(null);
        timestampAttribute.set(null);
    }

    public void setName(String nik) {
        nameAttribute.set(nik);
    }

    @Nullable
    public String getName() {
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

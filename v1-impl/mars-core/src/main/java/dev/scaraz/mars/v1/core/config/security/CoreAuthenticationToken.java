package dev.scaraz.mars.v1.core.config.security;

import dev.scaraz.mars.v1.core.domain.credential.User;
import dev.scaraz.mars.v1.core.util.AuthSource;
import dev.scaraz.mars.v1.core.util.DelegateUser;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class CoreAuthenticationToken extends AbstractAuthenticationToken {

    @Getter
    private final AuthSource source;
    private final DelegateUser principal;
    private final String credentials;

    public CoreAuthenticationToken(AuthSource source, User user, String token) {
        super(user.getRoles());
        this.source = source;
        this.principal = new DelegateUser(user);
        this.credentials = token;

        if (user.isActive()) super.setAuthenticated(true);
    }

    @Override
    public String getCredentials() {
        return this.credentials;
    }

    @Override
    public DelegateUser getPrincipal() {
        return this.principal;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
    }
}

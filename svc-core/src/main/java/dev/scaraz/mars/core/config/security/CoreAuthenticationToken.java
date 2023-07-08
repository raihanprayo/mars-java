package dev.scaraz.mars.core.config.security;

import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.util.AuthSource;
import dev.scaraz.mars.core.util.DelegateUser;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class CoreAuthenticationToken extends AbstractAuthenticationToken {

    @Getter
    private final AuthSource source;
    private final DelegateUser principal;
    private final String credentials;

    public CoreAuthenticationToken(AuthSource source, Account account, String token) {
        super(account.getRoles());
        this.source = source;
        this.principal = new DelegateUser(account);
        this.credentials = token;

        if (account.isActive()) super.setAuthenticated(true);
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

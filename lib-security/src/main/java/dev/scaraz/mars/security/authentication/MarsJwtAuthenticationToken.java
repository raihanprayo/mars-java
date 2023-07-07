package dev.scaraz.mars.security.authentication;

import dev.scaraz.mars.security.constants.AccessType;
import dev.scaraz.mars.security.authentication.identity.MarsAccessToken;

public class MarsJwtAuthenticationToken extends MarsAuthenticationToken<MarsAccessToken> {

    private final String rawToken;

    public MarsJwtAuthenticationToken(String rawToken, MarsAccessToken token) {
        super(AccessType.WEB, token);
        this.rawToken = rawToken;
    }

    public boolean isRefreshToken() {
        return getPrincipal().isRefreshToken();
    }

    @Override
    public String getName() {
        return getPrincipal().getName();
    }

    @Override
    public Object getCredentials() {
        return rawToken;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
    }
}

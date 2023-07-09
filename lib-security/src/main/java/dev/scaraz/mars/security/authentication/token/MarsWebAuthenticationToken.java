package dev.scaraz.mars.security.authentication.token;

import dev.scaraz.mars.security.authentication.identity.MarsWebToken;
import dev.scaraz.mars.security.constants.AccessType;
import lombok.Getter;

public class MarsWebAuthenticationToken extends MarsAuthenticationToken<MarsWebToken> {

    @Getter
    private final String sessionId;

    @Getter
    private final String rawToken;

    public MarsWebAuthenticationToken(String sessionId, String rawToken, MarsWebToken token) {
        super(AccessType.WEB, token);
        this.rawToken = rawToken;
        this.sessionId = sessionId;
    }

    public MarsWebAuthenticationToken(String rawToken, MarsWebToken token) {
        this(null, rawToken, token);
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
        return null;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
    }
}

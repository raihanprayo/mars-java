package dev.scaraz.mars.security.authentication;

import dev.scaraz.mars.security.jwt.MarsAccessToken;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class MarsJwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String rawToken;
    private final MarsAccessToken claims;

    public MarsJwtAuthenticationToken(String rawToken, MarsAccessToken token) {
        super(token.getRoles());
        this.claims = token;
        this.rawToken = rawToken;
        super.setAuthenticated(getAuthorities().size() > 0);
    }

    @Override
    public String getName() {
        return claims.getNik();
    }

    @Override
    public Object getCredentials() {
        return rawToken;
    }

    @Override
    public Object getPrincipal() {
        return claims;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
    }
}

package dev.scaraz.mars.security.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.ArrayList;

public class MarsBearerAuthenticationToken extends AbstractAuthenticationToken {

    private final String token;

    public MarsBearerAuthenticationToken(String token) {
        super(new ArrayList<>());
        this.token = token;
        super.setAuthenticated(false);
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
    }

}

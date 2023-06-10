package dev.scaraz.mars.security;

import dev.scaraz.mars.security.jwt.JwtAccessToken;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import javax.security.auth.Subject;

public class MarsAuthenticationToken extends AbstractAuthenticationToken {

    private final JwtAccessToken principal;

    public MarsAuthenticationToken(JwtAccessToken principal) {
        super(principal.getRoles());
        this.principal = principal;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public JwtAccessToken getPrincipal() {
        return this.principal;
    }

    @Override
    public String getName() {
        return principal.getNik();
    }

    @Override
    public boolean implies(Subject subject) {
        return super.implies(subject);
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        super.setAuthenticated(false);
    }

    @Override
    public Object getDetails() {
        return super.getDetails();
    }
}

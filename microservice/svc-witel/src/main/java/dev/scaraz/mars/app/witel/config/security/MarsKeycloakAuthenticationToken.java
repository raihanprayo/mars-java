package dev.scaraz.mars.app.witel.config.security;

import org.keycloak.adapters.spi.KeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class MarsKeycloakAuthenticationToken extends KeycloakAuthenticationToken {
    public MarsKeycloakAuthenticationToken(KeycloakAccount account, boolean interactive) {
        super(account, interactive);
    }

    public MarsKeycloakAuthenticationToken(KeycloakAccount account, boolean interactive, Collection<? extends GrantedAuthority> authorities) {
        super(account, interactive, authorities);
    }
}

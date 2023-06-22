package dev.scaraz.mars.core.v2.config.security;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public final class UserContext {

    private UserContext() {
    }

    @Nullable
    private static KeycloakPrincipal<KeycloakSecurityContext> getPrincipal() {
        Authentication auth = SecurityContextHolder.getContext()
                .getAuthentication();

        if (auth == null) return null;
        KeycloakAuthenticationToken authToken = (KeycloakAuthenticationToken) auth;
        return (KeycloakPrincipal<KeycloakSecurityContext>) authToken.getPrincipal();
    }

    @Nullable
    public static AccessToken getAccessToken() {
        KeycloakPrincipal<KeycloakSecurityContext> principal = getPrincipal();
        if (principal != null) return principal.getKeycloakSecurityContext().getToken();
        return null;
    }

    public static String getCurrentUsername() {
        return Optional.ofNullable(getPrincipal())
                .map(KeycloakPrincipal::getKeycloakSecurityContext)
                .map(KeycloakSecurityContext::getToken)
                .map(AccessToken::getPreferredUsername)
                .orElse("system");
    }

}

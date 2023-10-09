package dev.scaraz.mars.app.administration.config.security;

import dev.scaraz.mars.app.administration.web.dto.UserAccount;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    private SecurityUtil() {
    }

    public static KeycloakAuthenticationToken getAuthentication() {
        return (KeycloakAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    }

    public static KeycloakPrincipal<KeycloakSecurityContext> getPrincipal() {
        KeycloakAuthenticationToken authentication = getAuthentication();
        if (authentication != null) {
            if (authentication.getPrincipal() instanceof KeycloakPrincipal)
                return (KeycloakPrincipal<KeycloakSecurityContext>) authentication.getPrincipal();
        }

        return null;
    }

    public static boolean isAuthorized() {
        return getPrincipal() != null;
    }

    public static AccessToken getToken() {
        return isAuthorized() ? getPrincipal().getKeycloakSecurityContext().getToken() : null;
    }

    public static UserAccount getAccount() {
        if (!isAuthorized()) return null;
        return new UserAccount(getToken());
    }

}

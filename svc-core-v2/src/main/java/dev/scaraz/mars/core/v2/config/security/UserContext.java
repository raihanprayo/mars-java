package dev.scaraz.mars.core.v2.config.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Optional;

public final class UserContext {

    private UserContext() {
    }

    private static JwtAuthenticationToken getPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken) return (JwtAuthenticationToken) auth;
        return null;
    }

    public static String currentUsername() {
        return Optional.ofNullable(getPrincipal())
                .map(t -> (String) t.getTokenAttributes().get("nik"))
                .orElse(null);
    }

}

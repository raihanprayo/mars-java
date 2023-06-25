package dev.scaraz.mars.core.v2.config.security;

import dev.scaraz.mars.security.authentication.MarsJwtAuthenticationToken;
import dev.scaraz.mars.security.jwt.MarsAccessToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public final class UserContext {

    private UserContext() {
    }

    private static MarsJwtAuthenticationToken getPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof MarsJwtAuthenticationToken) return (MarsJwtAuthenticationToken) auth;
        return null;
    }

    public static MarsAccessToken getAccessToken() {
        return Optional.ofNullable(getPrincipal())
                .map(t -> (MarsAccessToken) t.getPrincipal())
                .orElse(null);
    }

    public static String getSubject() {
        return Optional.ofNullable(getAccessToken())
                .map(MarsAccessToken::getSub)
                .orElse(null);
    }

    public static String getUsername() {
        return Optional.ofNullable(getPrincipal())
                .map(MarsJwtAuthenticationToken::getName)
                .orElse(null);
    }

}

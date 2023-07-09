package dev.scaraz.mars.security;

import dev.scaraz.mars.common.exception.web.AccessDeniedException;
import dev.scaraz.mars.security.authentication.identity.MarsAuthentication;
import dev.scaraz.mars.security.authentication.token.MarsAuthenticationToken;
import dev.scaraz.mars.security.constants.AccessType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

public final class MarsUserContext {
    private MarsUserContext() {
    }

    public static MarsAuthenticationToken<MarsAuthentication> getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof MarsAuthenticationToken) {
            if (authentication.isAuthenticated())
                return (MarsAuthenticationToken<MarsAuthentication>) authentication;
        }

        throw AccessDeniedException.args("Invalid authentication token");
    }

    public static boolean isUserPresent() {
        try {
            getAuthentication();
            return true;
        }
        catch (AccessDeniedException ex) {
            return false;
        }
    }

    public static boolean isTelegramAccess() {
        return getAuthentication().getAccess() == AccessType.TELEGRAM;
    }

    public static boolean isWebAccess() {
        return getAuthentication().getAccess() == AccessType.WEB;
    }

    public static MarsAuthentication getAccessToken() {
        return getAuthentication().getPrincipal();
    }


    public static String getId() {
        return getAccessToken().getId();
    }

    public static String getUsername() {
        return getAccessToken().getName();
    }

    public static Long getTelegram() {
        return getAccessToken().getTelegram();
    }

    public static boolean hasAnyRole(String... predicate) {
        List<String> roleList = List.of(predicate);
        return getAccessToken().getAuthorities().stream().anyMatch(r -> roleList.contains(r.getAuthority()));
    }

}

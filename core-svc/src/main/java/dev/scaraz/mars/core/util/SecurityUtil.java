package dev.scaraz.mars.core.util;

import dev.scaraz.mars.core.config.security.CoreAuthenticationToken;
import dev.scaraz.mars.core.domain.credential.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            CoreAuthenticationToken coreAuth = (CoreAuthenticationToken) auth;
            return coreAuth.getPrincipal().getUser();
        }
        return null;
    }

}

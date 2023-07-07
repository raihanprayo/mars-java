package dev.scaraz.mars.v1.core.util;

import dev.scaraz.mars.v1.core.config.security.CoreAuthenticationToken;
import dev.scaraz.mars.v1.core.domain.credential.User;
import dev.scaraz.mars.v1.core.query.UserQueryService;
import dev.scaraz.mars.security.authentication.identity.MarsAuthentication;
import dev.scaraz.mars.security.authentication.MarsAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class SecurityUtil {
    private static SecurityUtil INSTANCE;

    private final UserQueryService userQueryService;

    public SecurityUtil(UserQueryService userQueryService) {
        this.userQueryService = userQueryService;
        INSTANCE = this;
    }

    public static User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            if (auth instanceof CoreAuthenticationToken) {
                CoreAuthenticationToken coreAuth = (CoreAuthenticationToken) auth;
                return INSTANCE.userQueryService.findById(coreAuth.getPrincipal().getId());
            }
            else if (auth instanceof MarsAuthenticationToken) {
                MarsAuthenticationToken<MarsAuthentication> marsAuth = (MarsAuthenticationToken<MarsAuthentication>) auth;
                return INSTANCE.userQueryService.findById(marsAuth.getPrincipal().getId());
            }
        }
        return null;
    }

    public static boolean isUserPresent() {
        return Optional.ofNullable(getCurrentUser()).isPresent();
    }

}

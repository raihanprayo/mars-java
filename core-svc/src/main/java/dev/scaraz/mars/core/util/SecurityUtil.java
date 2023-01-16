package dev.scaraz.mars.core.util;

import dev.scaraz.mars.core.config.security.CoreAuthenticationToken;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.query.UserQueryService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

import static dev.scaraz.mars.common.tools.Translator.LANG_EN;


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
        if (auth != null && auth.isAuthenticated() && auth instanceof CoreAuthenticationToken) {
            CoreAuthenticationToken coreAuth = (CoreAuthenticationToken) auth;
            DelegateUser principal = coreAuth.getPrincipal();
            return INSTANCE.userQueryService.findById(principal.getId());
        }
        return null;
    }

    public static Locale getUserLocale() {
        User user = getCurrentUser();
        if (user == null) return LANG_EN;
        return user.getSetting().getLang();
    }

}

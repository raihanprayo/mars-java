package dev.scaraz.mars.core.util;

import dev.scaraz.mars.core.config.security.CoreAuthenticationToken;
import dev.scaraz.mars.core.domain.credential.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Locale;

import static dev.scaraz.mars.common.tools.Translator.LANG_EN;


public class SecurityUtil {

    public static User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth instanceof CoreAuthenticationToken) {
            CoreAuthenticationToken coreAuth = (CoreAuthenticationToken) auth;
            return coreAuth.getPrincipal().getUser();
        }
        return null;
    }

    public static Locale getUserLocale() {
        User user = getCurrentUser();
        if (user == null) return LANG_EN;
        return user.getSetting().getLang();
    }

}

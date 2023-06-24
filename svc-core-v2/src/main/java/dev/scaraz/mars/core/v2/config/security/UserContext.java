package dev.scaraz.mars.core.v2.config.security;

public final class UserContext {

    private UserContext() {
    }

//    @Nullable
//    private static KeycloakPrincipal<KeycloakSecurityContext> getPrincipal() {
//        Authentication auth = SecurityContextHolder.getContext()
//                .getAuthentication();
//
//        if (auth == null) return null;
//        KeycloakAuthenticationToken authToken = (KeycloakAuthenticationToken) auth;
//        return (KeycloakPrincipal<KeycloakSecurityContext>) authToken.getPrincipal();
//    }
//
//    public boolean hasAnyAccess() {
//        return getPrincipal() != null;
//    }
//
//    @Nullable
//    public static AccessToken getAccessToken() {
//        KeycloakPrincipal<KeycloakSecurityContext> principal = getPrincipal();
//        if (principal != null) return principal.getKeycloakSecurityContext().getToken();
//        return null;
//    }
//
//    public static String getCurrentUsername() {
//        return Optional.ofNullable(getAccessToken())
//                .map(AccessToken::getPreferredUsername)
//                .orElse("system");
//    }
//
//    public static boolean isAdmin() {
//        return Optional.ofNullable(getAccessToken())
//                .map(a -> a.getRealmAccess().isUserInRole(AppConstants.Authority.ADMIN_ROLE))
//                .orElse(false);
//    }
//
//    public static boolean isUser() {
//        return Optional.ofNullable(getAccessToken())
//                .map(a -> a.getRealmAccess().isUserInRole(AppConstants.Authority.USER_ROLE))
//                .orElse(false);
//    }
//
//    public static boolean isAgent() {
//        return Optional.ofNullable(getAccessToken())
//                .map(a -> a.getRealmAccess().isUserInRole(AppConstants.Authority.AGENT_ROLE))
//                .orElse(false);
//    }

}

package dev.scaraz.mars.security.authentication.provider;

import dev.scaraz.mars.security.authentication.token.MarsLoginAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

public class MarsLoginAuthenticaitonProvider extends AbstractUserDetailsAuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(MarsLoginAuthenticationToken.class, authentication,
                () -> this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.onlySupports",
                        "Only UsernamePasswordAuthenticationToken is supported"));
        String username = determineUsername(authentication);
        boolean cacheWasUsed = true;
        UserDetails user = this.getUserCache().getUserFromCache(username);
        if (user == null) {
            cacheWasUsed = false;
            try {
                user = retrieveUser(username, (MarsLoginAuthenticationToken) authentication);
            }
            catch (UsernameNotFoundException ex) {
                this.logger.debug("Failed to find user '" + username + "'");
                if (!this.hideUserNotFoundExceptions) {
                    throw ex;
                }
                throw new BadCredentialsException(this.messages
                        .getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
            }
            Assert.notNull(user, "retrieveUser returned null - a violation of the interface contract");
        }
        try {
            this.getPreAuthenticationChecks().check(user);
            additionalAuthenticationChecks(user, (MarsLoginAuthenticationToken) authentication);
        }
        catch (AuthenticationException ex) {
            if (!cacheWasUsed) {
                throw ex;
            }
            // There was a problem, so try again after checking
            // we're using latest data (i.e. not from the cache)
            cacheWasUsed = false;
            user = retrieveUser(username, (MarsLoginAuthenticationToken) authentication);
            this.getPreAuthenticationChecks().check(user);
            additionalAuthenticationChecks(user, (MarsLoginAuthenticationToken) authentication);
        }
        this.getPostAuthenticationChecks().check(user);
        if (!cacheWasUsed) {
            this.getUserCache().putUserInCache(user);
        }
        Object principalToReturn = user;
        if (this.isForcePrincipalAsString()) {
            principalToReturn = user.getUsername();
        }
        return createSuccessAuthentication(principalToReturn, authentication, user);
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return MarsLoginAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private String determineUsername(Authentication authentication) {
        return (authentication.getPrincipal() == null) ? "NONE_PROVIDED" : authentication.getName();
    }

}

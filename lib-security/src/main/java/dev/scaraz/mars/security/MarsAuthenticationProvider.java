package dev.scaraz.mars.security;

import dev.scaraz.mars.security.auth.JwtAuthenticationToken;
import dev.scaraz.mars.security.auth.MarsAuthenticationToken;
import dev.scaraz.mars.security.jwt.JwtAccessToken;
import dev.scaraz.mars.security.jwt.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import java.util.List;

public class MarsAuthenticationProvider implements AuthenticationProvider {

    private static final List<Class<? extends Authentication>> SUPPORTED = List.of(
            MarsAuthenticationToken.class,
            JwtAuthenticationToken.class
    );

    @Override
    public Authentication authenticate(Authentication auth) throws AuthenticationException {
        if (auth instanceof MarsAuthenticationToken)
            return authenticateMarsToken((MarsAuthenticationToken) auth);
        else if (auth instanceof JwtAuthenticationToken)
            return authenticateJwtToken((JwtAuthenticationToken) auth);


        throw new InsufficientAuthenticationException("invalid authentication access");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SUPPORTED.stream()
                .anyMatch(authentication::isAssignableFrom);
    }

    private Authentication authenticateMarsToken(MarsAuthenticationToken auth) {
        return auth;
    }

    private Authentication authenticateJwtToken(JwtAuthenticationToken auth) {
        String token = (String) auth.getPrincipal();

        try {
            JwtAccessToken accessToken = JwtUtil.decode(token);
            if (accessToken.isRefreshToken())
                throw new IllegalArgumentException("Invalid token structure");

            MarsAuthenticationToken newAuthToken = new MarsAuthenticationToken(
                    token,
                    accessToken);

            newAuthToken.setDetails(auth.getDetails());
            return newAuthToken;
        }
        catch (ExpiredJwtException ex) {
            throw new CredentialsExpiredException(ex.getMessage());
        }
        catch (UnsupportedJwtException |
               MalformedJwtException |
               SignatureException |
               IllegalArgumentException ex) {
            throw new BadCredentialsException(ex.getMessage());
        }
    }

}

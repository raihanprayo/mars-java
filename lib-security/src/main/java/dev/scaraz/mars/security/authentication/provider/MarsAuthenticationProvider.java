package dev.scaraz.mars.security.authentication.provider;

import dev.scaraz.mars.security.authentication.token.MarsBearerAuthenticationToken;
import dev.scaraz.mars.security.authentication.token.MarsWebAuthenticationToken;
import dev.scaraz.mars.security.jwt.JwtParseResult;
import dev.scaraz.mars.security.jwt.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

@Slf4j
public class MarsAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.debug("AuthenticationProvider authenticate: {}", authentication.getClass().getSimpleName());
        if (authentication instanceof MarsBearerAuthenticationToken) {
            MarsBearerAuthenticationToken bearer = (MarsBearerAuthenticationToken) authentication;
            if (StringUtils.isBlank(bearer.getPrincipal().toString()))
                throw new AuthenticationCredentialsNotFoundException("empty JWT");

            JwtParseResult decoded = JwtUtil.decode((String) bearer.getPrincipal());
            if (decoded.getCode().isError()) {
                switch (decoded.getCode()) {
                    case ERR_EXPIRED:
                        throw new CredentialsExpiredException(decoded.getMessage());
                    case ERR_MALFORMED:
                        throw new BadCredentialsException(decoded.getMessage());
                    case ERR_UNSUPPORTED:
                    case ERR_SIGNATURE:
                    default:
                        throw new AuthenticationServiceException(decoded.getMessage());
                }
            }

            MarsWebAuthenticationToken token = new MarsWebAuthenticationToken(decoded.getRawToken(), decoded.getClaims());
            token.setDetails(bearer.getDetails());
            return token;
        }

        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return MarsBearerAuthenticationToken.class.isAssignableFrom(authentication);
    }

}

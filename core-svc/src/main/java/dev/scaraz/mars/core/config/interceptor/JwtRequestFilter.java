package dev.scaraz.mars.core.config.interceptor;

import dev.scaraz.mars.common.domain.response.JwtToken;
import dev.scaraz.mars.common.exception.web.UnauthorizedException;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.core.config.security.CoreAuthenticationToken;
import dev.scaraz.mars.core.config.security.JwtUtil;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.query.UserQueryService;
import dev.scaraz.mars.core.util.AuthSource;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    private static final String BEARER_PREFIX = "Bearer ";

    private final UserQueryService userQueryService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.isNoneBlank(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            try {
                String token = bearerToken.substring(BEARER_PREFIX.length());
                JwtToken jwt = JwtUtil.decode(token);

                if (jwt.isRefresher())
                    throw new UnauthorizedException("Invalid access token");

                User user = userQueryService.findById(jwt.getUserId());
                authenticate(user);
            }
            catch (SignatureException |
                   ExpiredJwtException |
                   MalformedJwtException |
                   UnsupportedJwtException |
                   IllegalArgumentException ex) {
                log.error("Jwt Parse Error", ex);
            }
        }
        filterChain.doFilter(request, response);
    }

    private void authenticate(User user) {
        if (!user.isActive())
            throw new UnauthorizedException("Please contact your administrator to activate your account");

        SecurityContextHolder.getContext()
                .setAuthentication(new CoreAuthenticationToken(AuthSource.JWT, user));

        LocaleContextHolder.setLocale(user.getSetting().getLang(), true);
    }
}

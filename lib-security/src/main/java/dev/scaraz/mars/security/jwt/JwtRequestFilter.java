package dev.scaraz.mars.security.jwt;

import dev.scaraz.mars.security.MarsSecurityProperties;
import dev.scaraz.mars.security.auth.JwtAuthenticationToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final AuthenticationManager authManager;
    private final MarsSecurityProperties securityProperties;

    private final WebAuthenticationDetailsSource authenticationDetailsSource = new WebAuthenticationDetailsSource();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");

        boolean isUnauthenticated = !(SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().isAuthenticated());

        if (StringUtils.isNoneBlank(authorization) && isUnauthenticated) {
            String prefix = securityProperties.getJwt().getTokenPrefix().trim() + " ";
            String token = authorization.substring(prefix.length()).trim();

            if (StringUtils.isNoneBlank(token)) {
                SecurityContext ctx = SecurityContextHolder.createEmptyContext();
                JwtAuthenticationToken authToken = new JwtAuthenticationToken(token);
                authToken.setDetails(authenticationDetailsSource.buildDetails(request));

                ctx.setAuthentication(authManager.authenticate(authToken));
                SecurityContextHolder.setContext(ctx);
            }
        }
        filterChain.doFilter(request, response);
    }

}

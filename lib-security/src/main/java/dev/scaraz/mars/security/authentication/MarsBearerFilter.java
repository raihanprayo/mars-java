package dev.scaraz.mars.security.authentication;

import dev.scaraz.mars.security.authentication.provider.MarsAuthenticationProvider;
import dev.scaraz.mars.security.authentication.token.MarsBearerAuthenticationToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class MarsBearerFilter extends OncePerRequestFilter {

    private final MarsAuthenticationProvider authenticationProvider;
    private final WebAuthenticationDetailsSource detailsSource = new WebAuthenticationDetailsSource();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        AntPathRequestMatcher tokenPath = new AntPathRequestMatcher("/auth/token");
        if (tokenPath.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorization = request.getHeader("Authorization");
        if (StringUtils.isBlank(authorization)) {
            filterChain.doFilter(request, response);
        }
        else {
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                filterChain.doFilter(request, response);
                return;
            }

            String[] bearers = authorization.split(" ");
            if (bearers.length < 2) {
                filterChain.doFilter(request, response);
                return;
            }
            else if (bearers[1].equalsIgnoreCase("undefined")) {
                filterChain.doFilter(request, response);
                return;
            }

            log.debug("JWT Token: {}", authorization);
            MarsBearerAuthenticationToken token = new MarsBearerAuthenticationToken(authorization.substring("Bearer ".length()));
            token.setDetails(detailsSource.buildDetails(request));

            SecurityContext ctx = SecurityContextHolder.createEmptyContext();
            ctx.setAuthentication(authenticationProvider.authenticate(token));
            SecurityContextHolder.setContext(ctx);

            filterChain.doFilter(request, response);
        }
    }
}

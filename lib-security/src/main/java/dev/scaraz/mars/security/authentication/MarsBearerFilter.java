package dev.scaraz.mars.security.authentication;

import dev.scaraz.mars.security.authentication.token.MarsBearerAuthenticationToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MarsBearerFilter extends OncePerRequestFilter {

    private final WebAuthenticationDetailsSource detailsSource = new WebAuthenticationDetailsSource();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        if (StringUtils.isBlank(authorization)) {
            filterChain.doFilter(request, response);
        }
        else {
            MarsBearerAuthenticationToken token = new MarsBearerAuthenticationToken(authorization.substring("Bearer ".length()));
            token.setDetails(detailsSource.buildDetails(request));

            SecurityContext ctx = SecurityContextHolder.createEmptyContext();
            ctx.setAuthentication(token);
            SecurityContextHolder.setContext(ctx);

            filterChain.doFilter(request, response);
        }
    }
}

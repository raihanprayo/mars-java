package dev.scaraz.mars.security.jwt;

import com.google.gson.Gson;
import dev.scaraz.mars.common.exception.web.UnauthorizedException;
import dev.scaraz.mars.security.MarsAuthenticationToken;
import dev.scaraz.mars.security.MarsSecurityProperties;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

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

            try {
                JwtAccessToken accessToken = JwtUtil.decode(token);
                MarsAuthenticationToken authToken = new MarsAuthenticationToken(
                        token,
                        accessToken);

                authToken.setDetails(authenticationDetailsSource.buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                filterChain.doFilter(request, response);
            }
            catch (ExpiredJwtException ex) {
                throw new UnauthorizedException("Token expired");
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        else filterChain.doFilter(request, response);
    }

}

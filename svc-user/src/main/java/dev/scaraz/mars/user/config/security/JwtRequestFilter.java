package dev.scaraz.mars.user.config.security;

import dev.scaraz.mars.common.domain.general.AccessToken;
import dev.scaraz.mars.common.tools.security.JwtProvider;
import dev.scaraz.mars.user.service.app.UserService;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private JwtProvider jwtProvider;

    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    public void setJwtProvider(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String bearer = request.getHeader("Authorization");
        if (StringUtils.isNoneBlank(bearer) && bearer.startsWith("Bearer ")) {
            String token = bearer.substring("Bearer ".length());
            try {
                Claims claims = jwtProvider.decode(token).getBody();
                AccessToken parse = jwtProvider.parse(claims);
                parse.setToken(token);

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        parse,
                        null,
                        parse.getRoles().stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList())
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        filterChain.doFilter(request, response);
    }

}

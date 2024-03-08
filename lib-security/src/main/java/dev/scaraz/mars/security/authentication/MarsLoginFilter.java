package dev.scaraz.mars.security.authentication;

import dev.scaraz.mars.security.authentication.token.MarsLoginAuthenticationToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class MarsLoginFilter extends AbstractAuthenticationProcessingFilter {

    protected MarsLoginFilter(AuthenticationManager authenticationManager) {
        super(new AntPathRequestMatcher("/auth/login"), authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST"))
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());

        RequestParams params = getParameters(request);
        if (params.username == null || params.password == null)
            throw new InsufficientAuthenticationException("insufficient credential login identity");

        MarsLoginAuthenticationToken token = new MarsLoginAuthenticationToken(params.username, params.password, params.confirmed);
        token.setDetails(authenticationDetailsSource.buildDetails(request));
        return getAuthenticationManager().authenticate(token);
    }

    private RequestParams getParameters(HttpServletRequest request) {
        RequestParams params = new RequestParams();

        params.username = request.getParameter("username");
        if (params.username != null) params.username = params.username.trim();

        params.password = request.getParameter("password");
        if (params.password != null) params.password = params.password.trim();

        String confirmed = request.getParameter("confirmed");
        params.confirmed = StringUtils.isNoneBlank(confirmed) && Boolean.parseBoolean(confirmed.trim());
        return params;
    }

    private static class RequestParams {
        private String username;
        private String password;
        private boolean confirmed;
    }

}

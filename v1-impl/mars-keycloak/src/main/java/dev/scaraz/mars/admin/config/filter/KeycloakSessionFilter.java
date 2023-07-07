package dev.scaraz.mars.admin.config.filter;

import org.keycloak.common.ClientConnection;
import org.keycloak.services.filters.AbstractRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class KeycloakSessionFilter extends AbstractRequestFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws UnsupportedEncodingException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;

        req.setCharacterEncoding("UTF-8");
        filter(connection(req), session -> {
            try {
                chain.doFilter(servletRequest, servletResponse);
            }
            catch (IOException | ServletException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private ClientConnection connection(HttpServletRequest request) {
        return new ClientConnection() {
            @Override
            public String getRemoteAddr() {
                return request.getRemoteAddr();
            }

            @Override
            public String getRemoteHost() {
                return request.getRemoteHost();
            }

            @Override
            public int getRemotePort() {
                return request.getRemotePort();
            }

            @Override
            public String getLocalAddr() {
                return request.getLocalAddr();
            }

            @Override
            public int getLocalPort() {
                return request.getLocalPort();
            }
        };
    }

}

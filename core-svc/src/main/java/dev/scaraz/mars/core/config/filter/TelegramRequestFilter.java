package dev.scaraz.mars.core.config.filter;

import dev.scaraz.mars.common.exception.web.NotFoundException;
import dev.scaraz.mars.common.exception.web.UnauthorizedException;
import dev.scaraz.mars.core.config.security.CoreAuthenticationToken;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.query.UserQueryService;
import dev.scaraz.mars.core.util.AuthSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor

@Component
public class TelegramRequestFilter extends OncePerRequestFilter {

    private final UserQueryService userQueryService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String telegram = request.getHeader("Telegram");
        if (telegram != null) {
            long tgId = Long.parseLong(telegram);

            try {
                User user = userQueryService.findByTelegramId(tgId);
                SecurityContextHolder.getContext()
                        .setAuthentication(new CoreAuthenticationToken(AuthSource.TELEGRAM, user));
            }
            catch (NotFoundException ex) {
                throw new UnauthorizedException("user telegram not registered");
            }
        }

        filterChain.doFilter(request, response);
    }
}

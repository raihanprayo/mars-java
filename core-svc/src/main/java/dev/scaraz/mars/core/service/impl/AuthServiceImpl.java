package dev.scaraz.mars.core.service.impl;

import dev.scaraz.mars.common.config.properties.MarsProperties;
import dev.scaraz.mars.common.domain.request.AuthReqDTO;
import dev.scaraz.mars.common.domain.request.TicketStatusFormDTO;
import dev.scaraz.mars.common.domain.response.AuthResDTO;
import dev.scaraz.mars.common.domain.response.JwtResult;
import dev.scaraz.mars.common.domain.response.JwtToken;
import dev.scaraz.mars.common.exception.telegram.TgUnauthorizedError;
import dev.scaraz.mars.common.exception.web.*;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.common.tools.filter.type.TcStatusFilter;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.core.config.datasource.AuditProvider;
import dev.scaraz.mars.core.config.security.CoreAuthenticationToken;
import dev.scaraz.mars.core.config.security.JwtUtil;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.domain.order.AgentWorklog;
import dev.scaraz.mars.core.query.AgentQueryService;
import dev.scaraz.mars.core.query.UserQueryService;
import dev.scaraz.mars.core.query.criteria.AgentWorklogCriteria;
import dev.scaraz.mars.core.query.criteria.AgentWorkspaceCriteria;
import dev.scaraz.mars.core.repository.credential.UserRepo;
import dev.scaraz.mars.core.service.AuthService;
import dev.scaraz.mars.core.service.credential.UserApprovalService;
import dev.scaraz.mars.core.service.credential.UserService;
import dev.scaraz.mars.core.service.order.flow.DispatchFlowService;
import dev.scaraz.mars.core.util.AuthSource;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import static dev.scaraz.mars.common.utils.AppConstants.Auth.*;

@Slf4j
@RequiredArgsConstructor

@Service
public class AuthServiceImpl implements AuthService {

    private final MarsProperties marsProperties;

    private final AuditProvider auditProvider;

    private final UserRepo userRepo;
    private final UserService userService;
    private final UserQueryService userQueryService;
    private final UserApprovalService userApprovalService;

    private final AgentQueryService agentQueryService;
    private final DispatchFlowService dispatchFlowService;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public AuthResDTO authenticate(AuthReqDTO authReq, String application) {
        User user = userQueryService.loadUserByUsername(authReq.getNik());

        boolean allowedLogin = user.hasAnyRole(
                AppConstants.Authority.ADMIN_ROLE,
                AppConstants.Authority.AGENT_ROLE
        );

        if (!allowedLogin)
            throw AccessDeniedException.args("Kamu tidak punya akses login ke dashboard");

        boolean hasPassword = user.getPassword() != null;
        if (!hasPassword) {
            if (!authReq.isConfirmed()) {
                return AuthResDTO.builder()
                        .code(PASSWORD_CONFIRMATION)
                        .build();
            }
            else {
                auditProvider.setName(user.getNik());
                user.setPassword(passwordEncoder.encode(authReq.getPassword()));
                if (authReq.getEmail() != null)
                    user.setEmail(authReq.getEmail());

                userService.save(user);
            }
        }
        else {
            boolean passwordMatch = passwordEncoder.matches(authReq.getPassword(), user.getPassword());
            if (!passwordMatch) {
                throw new UnauthorizedException("auth.user.invalid.password");
            }
            else if (!user.isActive())
                throw new UnauthorizedException("Silahkan menghubungi tim admin untuk mengaktifkan akunmu");
        }

        Instant issuedAt = Instant.now();
        JwtResult accessToken = JwtUtil.accessToken(user, application, issuedAt);
        JwtResult refreshToken = JwtUtil.refreshToken(user, application, issuedAt);
        try {
            return AuthResDTO.builder()
                    .code(SUCCESS)
                    .user(user)
                    .issuedAt(issuedAt.getEpochSecond())

                    .accessToken(accessToken.getToken())
                    .expiredAt(accessToken.getExpiredAt().getEpochSecond())
                    .refreshToken(refreshToken.getToken())
                    .refreshExpiredAt(refreshToken.getExpiredAt().getEpochSecond())
                    .build();
        }
        finally {
            auditProvider.clear();
        }
    }

    @Override
    public User authenticateFromBot(long telegramId) {
        try {
            User user = userQueryService.findByTelegramId(telegramId);
            if (!user.isActive())
                throw new TgUnauthorizedError("Your account is not active, try to contact your administrator");

            SecurityContextHolder.getContext().setAuthentication(
                    new CoreAuthenticationToken(AuthSource.TELEGRAM, user, null));
            return user;
        }
        catch (NotFoundException ex) {
            throw new TgUnauthorizedError(telegramId);
        }
    }

    @Override
    public AuthResDTO refresh(String refreshToken) {
        try {
            JwtToken decode = JwtUtil.decode(refreshToken);
            String audience = decode.getAudience();
            User user = userRepo.findById(decode.getUserId())
                    .orElseThrow();

            if (!decode.isRefresher())
                throw BadRequestException.args("Invalid refresh token");

            Instant now = Instant.now();
            JwtResult jwtAccessToken = JwtUtil.accessToken(user, audience, now);
            JwtResult jwtRefreshToken = JwtUtil.refreshToken(user, audience, now);
            return AuthResDTO.builder()
                    .code(SUCCESS)
                    .accessToken(jwtAccessToken.getToken())
                    .expiredAt(jwtAccessToken.getExpiredAt().getEpochSecond())
                    .refreshToken(jwtRefreshToken.getToken())
                    .refreshExpiredAt(jwtRefreshToken.getExpiredAt().getEpochSecond())
                    .build();
        }
        catch (ExpiredJwtException ex) {
            return AuthResDTO.builder()
                    .code(RELOGIN_REQUIRED)
                    .build();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void logout(User user, boolean confirmed) {
        List<AgentWorklog> worklogs = agentQueryService.findAllWorklogs(AgentWorklogCriteria.builder()
                .workspace(AgentWorkspaceCriteria.builder()
                        .userId(new StringFilter().setEq(user.getId()))
                        .build())
                .closeStatus(new TcStatusFilter()
                        .setSpecified(false))
                .build());

        int size = worklogs.size();
        if (size == 0) return;

        if (!confirmed)
            throw new LogoutException("wip", String.format("Found %s unsaved process", size));

        for (AgentWorklog worklog : worklogs) {
            dispatchFlowService.dispatch(
                    worklog.getWorkspace().getTicket().getNo(),
                    TicketStatusFormDTO.builder()
                            .status(TcStatus.DISPATCH)
                            .note("(confirmed) agent logout")
                            .build());
        }
    }

    @Override
    public boolean isUserRegistered(long telegramId) {
        try {
            userQueryService.findByTelegramId(telegramId);
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }

    @Override
    public boolean isUserInApproval(long telegramId) {
        return userApprovalService.existsByTelegramId(telegramId);
    }

}

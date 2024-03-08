package dev.scaraz.mars.core.service.impl;

import dev.scaraz.mars.common.config.DataSourceAuditor;
import dev.scaraz.mars.common.config.properties.MarsProperties;
import dev.scaraz.mars.common.domain.request.AuthReqDTO;
import dev.scaraz.mars.common.domain.request.ForgotReqDTO;
import dev.scaraz.mars.common.domain.request.TicketStatusFormDTO;
import dev.scaraz.mars.common.domain.response.AuthResDTO;
import dev.scaraz.mars.common.domain.response.ForgotResDTO;
import dev.scaraz.mars.common.domain.response.JwtResult;
import dev.scaraz.mars.common.exception.telegram.TgUnauthorizedError;
import dev.scaraz.mars.common.exception.web.*;
import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.common.tools.filter.type.StringFilter;
import dev.scaraz.mars.common.tools.filter.type.TcStatusFilter;
import dev.scaraz.mars.common.utils.AuthorityConstant;
import dev.scaraz.mars.common.utils.ConfigConstants;
import dev.scaraz.mars.core.config.event.app.AccountAccessEvent;
import dev.scaraz.mars.core.domain.cache.ForgotPassword;
import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.domain.order.AgentWorklog;
import dev.scaraz.mars.core.query.AccountQueryService;
import dev.scaraz.mars.core.query.AgentQueryService;
import dev.scaraz.mars.core.query.criteria.AgentWorklogCriteria;
import dev.scaraz.mars.core.query.criteria.AgentWorkspaceCriteria;
import dev.scaraz.mars.core.service.AuthService;
import dev.scaraz.mars.core.service.ConfigService;
import dev.scaraz.mars.core.service.credential.AccountApprovalService;
import dev.scaraz.mars.core.service.credential.AccountService;
import dev.scaraz.mars.core.service.credential.ForgotPasswordService;
import dev.scaraz.mars.core.service.order.flow.DispatchFlowService;
import dev.scaraz.mars.security.MarsPasswordEncoder;
import dev.scaraz.mars.security.MarsUserContext;
import dev.scaraz.mars.security.authentication.identity.MarsTelegramToken;
import dev.scaraz.mars.security.authentication.identity.MarsWebToken;
import dev.scaraz.mars.security.authentication.token.MarsTelegramAuthenticationToken;
import dev.scaraz.mars.security.authentication.token.MarsWebAuthenticationToken;
import dev.scaraz.mars.security.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static dev.scaraz.mars.common.utils.AppConstants.Auth.PASSWORD_CONFIRMATION;
import static dev.scaraz.mars.common.utils.AppConstants.Auth.SUCCESS;

@Slf4j
@RequiredArgsConstructor

@Service
public class AuthServiceImpl implements AuthService {

    private final MarsProperties marsProperties;

    //    private final UserRepo userRepo;
    private final ConfigService configService;

    private final AccountService accountService;
    private final AccountQueryService accountQueryService;
    private final AccountApprovalService accountApprovalService;

    private final AgentQueryService agentQueryService;
    private final DispatchFlowService dispatchFlowService;

    private final MarsPasswordEncoder passwordEncoder;

    private final ForgotPasswordService forgotPasswordService;

    @Override
    @Transactional
    public AuthResDTO authenticate(
            HttpServletRequest request,
            AuthReqDTO authReq,
            String application
    ) {
        Account account = accountQueryService.loadUserByUsername(authReq.getNik());

        boolean allowedLogin = account.hasAnyRole(
                AuthorityConstant.ADMIN_ROLE,
                AuthorityConstant.AGENT_ROLE
        );

        if (!allowedLogin)
            throw AccessDeniedException.args("Kamu tidak punya akses login ke dashboard");

        try {
            boolean hasPassword = account.getPassword() != null;
            if (!hasPassword) {
                if (!authReq.isConfirmed()) {
                    return AuthResDTO.builder()
                            .code(PASSWORD_CONFIRMATION)
                            .build();
                }
                else {
                    DataSourceAuditor.setUsername(account.getNik());
                    if (authReq.getEmail() != null)
                        account.setEmail(authReq.getEmail());

                    accountService.updatePassword(account, authReq.getPassword());
                    accountService.save(account);
                }
            }
            else {
                if (!account.isActive())
                    throw new UnauthorizedException("Silahkan menghubungi tim admin untuk mengaktifkan akunmu");

                boolean passwordMatch = passwordEncoder.matches(authReq.getPassword(), account.getCredential());
                if (!passwordMatch) {
                    throw new UnauthorizedException("auth.account.invalid.password");
                }
            }

            if (request.getRequestedSessionId() != null) {
                request.getSession(false).invalidate();
            }

            HttpSession session = request.getSession();
            AuthResDTO result = generateWebToken(session.getId(), account)
                    .user(account)
                    .code(SUCCESS)
                    .build();
            MarsWebAuthenticationToken authenticationToken = new MarsWebAuthenticationToken(
                    session.getId(),
                    result.getAccessToken(),
                    (MarsWebToken) result.getWebTokenPayloads()[0]
            );

            Duration timeout = configService.get(ConfigConstants.JWT_TOKEN_EXPIRED_DRT).getAsDuration();
            session.setMaxInactiveInterval((int) timeout.toSeconds());

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authenticationToken);
            SecurityContextHolder.setContext(context);

            AccountAccessEvent.details("WEB_LOGIN", account.getUsername())
                    .source("web")
                    .put("session_id", session.getId())
                    .put("token_id", result.getAccessTokenResult().getId())
                    .publish();
            return result;
        }
        finally {
            DataSourceAuditor.clear();
        }
    }

    @Override
    public Account authenticateFromBot(long telegramId) {
        return optionalAuthenticationFromBot(telegramId)
                .orElseThrow(() -> new TgUnauthorizedError(telegramId));
    }

    @Override
    public Optional<Account> optionalAuthenticationFromBot(long telegramId) {
        try {
            Account account = accountQueryService.findByTelegramId(telegramId);
            if (!account.isActive())
                throw new TgUnauthorizedError("Akunmu tidak aktif, silahkan menghubungi administrator mars");

            SecurityContextHolder.getContext().setAuthentication(
                    new MarsTelegramAuthenticationToken(MarsTelegramToken.builder()
                            .id(account.getId())
                            .name(account.getNik())
                            .phone(account.getPhone())
                            .witel(account.getWitel())
                            .sto(account.getSto())
                            .roles(account.getRoles())
                            .telegram(account.getTg().getId())
                            .build())
            );
            return Optional.of(account);
        }
        catch (NotFoundException ex) {
            return Optional.empty();
        }
    }

    @Override
    public AuthResDTO refresh(MarsWebAuthenticationToken authentication) {
        MarsWebToken accessToken = authentication.getPrincipal();
        Account account = accountQueryService.findById(accessToken.getSub());

        try {
            return generateWebToken(null, account)
                    .code(SUCCESS)
                    .build();
        }
        finally {
            AccountAccessEvent.details("WEB_CODE_TO_TOKEN", account.getUsername())
                    .source("web")
                    .publish();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void logout(HttpServletRequest request, Account account, boolean confirmed) {
        logout(account, confirmed);

        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
    }

    @Override
    public void logout(Account account, boolean confirmed) {
        List<AgentWorklog> worklogs = agentQueryService.findAllWorklogs(AgentWorklogCriteria.builder()
                .workspace(AgentWorkspaceCriteria.builder()
                        .userId(new StringFilter().setEq(account.getId()))
                        .build())
                .closeStatus(new TcStatusFilter()
                        .setSpecified(false))
                .build());

        int size = worklogs.size();
        if (size != 0) {
            if (!confirmed)
                throw new LogoutException("wip", String.format("Found %s unsaved process", size));

            DataSourceAuditor.setUsername(account.getNik());
            for (AgentWorklog worklog : worklogs) {
                dispatchFlowService.dispatch(
                        worklog.getWorkspace().getTicket().getNo(),
                        TicketStatusFormDTO.builder()
                                .status(TcStatus.DISPATCH)
                                .note("(confirmed) agent logout")
                                .build());
            }
        }

        boolean userPresent = MarsUserContext.isUserPresent();
        AccountAccessEvent.details("WEB_LOGOUT", account.getUsername())
                .source("web")
                .put("ticket_dispatch_count", size)
                .put("execution", userPresent ? "by-user" : "by-system")
                .publish();
    }

    @Override
    public ForgotResDTO forgotPasswordFlow(ForgotReqDTO f) {
        if (f.getState() == ForgotReqDTO.State.GENERATE) {
            Assert.notNull(f.getWith(), "No selected option");
            Assert.isTrue(StringUtils.isNoneBlank(f.getUsername()), "invalid identity matcher");

            Account account = accountQueryService.loadUserByUsername(f.getUsername());
            ForgotPassword fp = forgotPasswordService.generate(f.getWith(), account);
            return ForgotResDTO.builder()
                    .token(fp.getToken())
                    .length(fp.getOtp().length())
                    .expiredAt(fp.getExpiredAt())
                    .next(ForgotReqDTO.State.VALIDATE)
                    .build();
        }
        else if (f.getState() == ForgotReqDTO.State.VALIDATE) {
            Assert.isTrue(StringUtils.isNoneBlank(f.getOtp()), "OTP code cannot be null or empty");
            Assert.isTrue(StringUtils.isNoneBlank(f.getToken()), "Invalid reset request");

            boolean isValid = forgotPasswordService.validate(f.getToken(), f.getOtp());
            if (isValid) {
                return ForgotResDTO.builder()
                        .next(ForgotReqDTO.State.ACCOUNT_RESET)
                        .build();
            }
            else throw new BadRequestException("invalid otp");
        }
        else if (f.getState() == ForgotReqDTO.State.VALIDATE_TOKEN) {
            Assert.isTrue(StringUtils.isNoneBlank(f.getToken()), "Invalid reset request");

            boolean isValid = forgotPasswordService.validate(f.getToken(), f.getOtp());
            if (isValid) {
                return ForgotResDTO.builder()
                        .next(ForgotReqDTO.State.ACCOUNT_RESET)
                        .build();
            }
            else throw new BadRequestException("invalid otp");
        }
        else if (f.getState() == ForgotReqDTO.State.ACCOUNT_RESET) {
            Assert.isTrue(StringUtils.isNoneBlank(f.getNewPassword()), "NewPassword cannot be null or empty");
            Assert.isTrue(StringUtils.isNoneBlank(f.getToken()), "Invalid reset request");

            Claims body = JwtUtil.decodeToken(f.getToken()).getBody();
            forgotPasswordService.reset(body.getSubject(), f.getNewPassword());
            return ForgotResDTO.builder()
                    .next(ForgotReqDTO.State.DONE)
                    .build();
        }

        throw new IllegalStateException("Invalid forgot-password flow");
    }

    @Override
    public ForgotResDTO forgotRegenerateOtp(String token) {
        ForgotPassword regenerate = forgotPasswordService.regenerate(token);
        return ForgotResDTO.builder()
                .token(regenerate.getToken())
                .length(regenerate.getOtp().length())
                .build();
    }

    @Override
    public boolean isUserRegistered(long telegramId) {
        try {
            accountQueryService.findByTelegramId(telegramId);
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }

    @Override
    public boolean isUserInApproval(long telegramId) {
        return accountApprovalService.existsByTelegramId(telegramId);
    }


    private AuthResDTO.AuthResDTOBuilder generateWebToken(String sessionId, Account account) {
        log.info("GENERATE WEB TOKEN FOR USER -- {} | {}", account.getUsername(), account.getWitel());
        final String issuer = "web";
        Instant now = Instant.now();

        MarsWebToken.MarsWebTokenBuilder marsAccessToken = MarsWebToken.access()
                .iss(issuer)
                .sub(account.getId())
                .issuedAt(Date.from(now))
                .nik(account.getUsername())
                .witel(account.getWitel())
                .sto(account.getSto())
                .roles(account.getRoles());
        if (account.getTg() != null)
            marsAccessToken.telegram(account.getTg().getId());

        MarsWebToken webAccessToken = marsAccessToken.build();
        JwtResult access_token = JwtUtil.encode(sessionId, webAccessToken);

        MarsWebToken webRefreshToken = MarsWebToken.refresh()
                .iss(issuer)
                .sub(account.getId())
                .issuedAt(Date.from(now))
                .build();
        JwtResult refresh_token = JwtUtil.encode(webRefreshToken);

        return AuthResDTO.builder()
                .accessToken(access_token.getToken())
                .refreshToken(refresh_token.getToken())
                .issuedAt(now.toEpochMilli())
                .expiredAt(access_token.getExpiredAt().toEpochMilli())
                .refreshExpiredAt(refresh_token.getExpiredAt().toEpochMilli())

                .accessTokenResult(access_token)
                .refreshTokenResult(refresh_token)
                .webTokenPayloads(new Object[]{webAccessToken, webRefreshToken});
    }

}

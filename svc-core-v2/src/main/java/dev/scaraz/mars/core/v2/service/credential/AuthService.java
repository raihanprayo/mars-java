package dev.scaraz.mars.core.v2.service.credential;

import dev.scaraz.mars.common.domain.request.AuthReqDTO;
import dev.scaraz.mars.common.domain.response.AuthResDTO;
import dev.scaraz.mars.common.domain.response.JwtResult;
import dev.scaraz.mars.common.exception.web.AccessDeniedException;
import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.core.v2.config.datasource.DatasourceAuditor;
import dev.scaraz.mars.core.v2.domain.credential.Account;
import dev.scaraz.mars.core.v2.domain.credential.AccountCredential;
import dev.scaraz.mars.core.v2.query.credential.AccountQueryService;
import dev.scaraz.mars.core.v2.service.app.ConfigService;
import dev.scaraz.mars.core.v2.util.ConfigConstants;
import dev.scaraz.mars.security.MarsPasswordEncoder;
import dev.scaraz.mars.security.authentication.MarsJwtAuthenticationToken;
import dev.scaraz.mars.security.jwt.JwtUtil;
import dev.scaraz.mars.security.authentication.identity.MarsAccessToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final MarsPasswordEncoder passwordEncoder;

    private final AccountService accountService;
    private final AccountQueryService accountQueryService;
    private final ConfigService configService;

    @Transactional
    public AuthResDTO token(AuthReqDTO authReq) {
        Account account = (Account) accountQueryService.loadUserByUsername(authReq.getUsername());
        boolean canLogin = account.hasAnyRole(
                AppConstants.Authority.ADMIN_ROLE,
                AppConstants.Authority.AGENT_ROLE);

        log.trace("ACCOUNT CAN LOGIN ? {}", canLogin);
        if (!canLogin)
            throw AccessDeniedException.args("Kamu tidak punya akses login ke dashboard");

        try {
            log.trace("CHECK ACCOUNT CREDENTIAL");
            AccountCredential credential = account.getCredential();
            if (credential == null) {
                log.trace("NULL CREDENTIAL - SET AS NEW PASSWORD ? {}", authReq.isConfirmed());
                if (!authReq.isConfirmed()) {
                    return AuthResDTO.builder()
                            .code(AppConstants.Auth.PASSWORD_CONFIRMATION)
                            .build();
                }
                else {
                    DatasourceAuditor.setUsername(account.getUsername());
                    String algo = configService.get(ConfigConstants.CRD_DEFAULT_PASSWORD_ALGO_STR).getValue();
                    String secret = configService.get(ConfigConstants.CRD_DEFAULT_PASSWORD_SECRET_STR).getValue();
                    int iteration = configService.get(ConfigConstants.CRD_DEFAULT_PASSWORD_ITERATION_INT).getAsInt();

                    credential = AccountCredential.builder()
                            .priority(10)
                            .secret(secret)
                            .algorithm(algo)
                            .hashIteration(iteration)
                            .password(authReq.getPassword())
                            .account(account)
                            .build();

                    credential.setPassword(passwordEncoder.encode(credential));
                    credential = accountService.save(credential);
                    account.setCredentials(Set.of(credential));
                }
            }
            else {
                log.trace("CREDENTIAL EXIST - VALIDATING");
                if (!passwordEncoder.matches(authReq.getPassword(), credential))
                    throw BadRequestException.args("Password tidak sesuai");
            }

            if (!account.isEnabled())
                throw AccessDeniedException.args("Silahkan menghubungi tim admin untuk mengaktifkan akunmu");

            return generateWebToken(account)
                    .code(AppConstants.Auth.SUCCESS)
                    .user(account)
                    .build();
        }
        finally {
            DatasourceAuditor.clear();
        }
    }

    public AuthResDTO refresh(MarsJwtAuthenticationToken authentication) {
        MarsAccessToken accessToken = authentication.getPrincipal();
        if (!accessToken.isRefreshToken())
            throw new BadRequestException("format JWT tidak sesuai");

        Account account = accountQueryService.findById(accessToken.getSub());
        return generateWebToken(account)
                .code(AppConstants.Auth.SUCCESS)
                .build();
    }

    public AuthResDTO.AuthResDTOBuilder generateWebToken(Account account) {
        log.info("GENERATE WEB TOKEN FOR USER -- {} | {}", account.getUsername(), account.getMisc().getWitel());
        final String issuer = "web";
        Instant now = Instant.now();

        JwtResult access_token = JwtUtil.encode(MarsAccessToken.access()
                .iss(issuer)
                .sub(account.getId())
                .issuedAt(Date.from(now))
                .nik(account.getUsername())
                .telegram(account.getMisc().getTelegram())
                .witel(account.getMisc().getWitel())
                .sto(account.getMisc().getSto())
                .roles(account.getRoles())
                .build());

        JwtResult refresh_token = JwtUtil.encode(MarsAccessToken.refresh()
                .iss(issuer)
                .sub(account.getId())
                .issuedAt(Date.from(now))
                .build());

        return AuthResDTO.builder()
                .accessToken(access_token.getToken())
                .refreshToken(refresh_token.getToken())
                .issuedAt(now.toEpochMilli())
                .expiredAt(access_token.getExpiredAt().toEpochMilli())
                .refreshExpiredAt(refresh_token.getExpiredAt().toEpochMilli());
    }

}

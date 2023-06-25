package dev.scaraz.mars.core.v2.service.credential.impl;

import dev.scaraz.mars.common.domain.request.AuthReqDTO;
import dev.scaraz.mars.common.domain.response.AuthResDTO;
import dev.scaraz.mars.common.exception.web.AccessDeniedException;
import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.core.v2.config.datasource.DatasourceAuditor;
import dev.scaraz.mars.core.v2.domain.credential.Account;
import dev.scaraz.mars.core.v2.domain.credential.AccountCredential;
import dev.scaraz.mars.core.v2.query.credential.AccountQueryService;
import dev.scaraz.mars.core.v2.service.app.ConfigService;
import dev.scaraz.mars.core.v2.service.credential.AccountService;
import dev.scaraz.mars.core.v2.service.credential.AuthService;
import dev.scaraz.mars.core.v2.util.ConfigConstants;
import dev.scaraz.mars.security.MarsPasswordEncoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final JwtEncoder jwtEncoder;
    private final MarsPasswordEncoder passwordEncoder;

    private final AccountService accountService;
    private final AccountQueryService accountQueryService;
    private final ConfigService configService;

    @Override
    @Transactional
    public AuthResDTO token(AuthReqDTO authReq) {
        Account account = (Account) accountQueryService.loadUserByUsername(authReq.getUsername());
        boolean canLogin = account.hasAnyRole(
                AppConstants.Authority.ADMIN_ROLE,
                AppConstants.Authority.AGENT_ROLE);

        if (!canLogin)
            throw AccessDeniedException.args("Kamu tidak punya akses login ke dashboard");

        try {
            AccountCredential credential = account.getCredential();
            if (credential == null) {
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
                if (!passwordEncoder.matches(authReq.getPassword(), credential))
                    throw BadRequestException.args("Password tidak sesuai");
            }

            if (!account.isEnabled())
                throw AccessDeniedException.args("Silahkan menghubungi tim admin untuk mengaktifkan akunmu");

            return generateToken(account)
                    .code(AppConstants.Auth.SUCCESS)
                    .user(account)
                    .build();
        }
        finally {
            DatasourceAuditor.clear();
        }
    }

    private AuthResDTO.AuthResDTOBuilder generateToken(Account account) {
        Duration accessTokenDuration = configService.get(ConfigConstants.JWT_TOKEN_EXPIRED_DRT).getAs(Duration::parse);
        Duration refreshTokenDuration = configService.get(ConfigConstants.JWT_TOKEN_REFRESH_EXPIRED_DRT).getAs(Duration::parse);
        Instant now = Instant.now();

        Instant acs_expired = now.plus(accessTokenDuration.toMillis(), ChronoUnit.MILLIS);
        JwtClaimsSet.Builder acs_claims = JwtClaimsSet.builder()
                .subject(account.getId())
                .issuer("acs")
                .expiresAt(acs_expired)
                .claim("nik", account.getUsername())
                .claim("witel", account.getMisc().getWitel().name())
                .issuedAt(now);

        if (account.getMisc().getTelegram() != null)
            acs_claims.claim("tg", account.getMisc().getTelegram());
        if (account.getMisc().getSto() != null)
            acs_claims.claim("sto", account.getMisc().getSto());

        Jwt access_token = jwtEncoder.encode(JwtEncoderParameters.from(acs_claims.build()));

        Instant rfs_expired = now.plus(refreshTokenDuration.toMillis(), ChronoUnit.MILLIS);
        Jwt refresh_token = jwtEncoder.encode(JwtEncoderParameters.from(JwtClaimsSet.builder()
                .subject(account.getId())
                .issuer("rfs")
                .expiresAt(rfs_expired)
                .issuedAt(now)
                .build()));

        return AuthResDTO.builder()
                .accessToken(access_token.getTokenValue())
                .refreshToken(refresh_token.getTokenValue())
                .issuedAt(now.toEpochMilli())
                .expiredAt(acs_expired.toEpochMilli())
                .refreshExpiredAt(rfs_expired.toEpochMilli());
    }

}

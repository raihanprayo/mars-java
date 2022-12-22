package dev.scaraz.mars.core.service.credential.impl;

import dev.scaraz.mars.common.domain.request.AuthReqDTO;
import dev.scaraz.mars.common.domain.response.AuthResDTO;
import dev.scaraz.mars.common.domain.response.JwtResult;
import dev.scaraz.mars.common.domain.response.JwtToken;
import dev.scaraz.mars.common.exception.web.AccessDeniedException;
import dev.scaraz.mars.common.exception.web.UnauthorizedException;
import dev.scaraz.mars.core.config.datasource.AuditProvider;
import dev.scaraz.mars.core.config.security.JwtUtil;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.query.UserQueryService;
import dev.scaraz.mars.core.repository.credential.UserCredentialRepo;
import dev.scaraz.mars.core.repository.credential.UserRepo;
import dev.scaraz.mars.core.service.credential.UserAuthService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static dev.scaraz.mars.common.utils.AppConstants.Auth.*;

@Slf4j
@RequiredArgsConstructor

@Service
public class UserAuthServiceImpl implements UserAuthService {

    private final AuditProvider auditProvider;
    private final UserRepo userRepo;
    private final UserCredentialRepo userCredentialRepo;
    private final UserQueryService userQueryService;

    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResDTO login(AuthReqDTO authReq, String application) {
        User user = userQueryService.loadUserByUsername(authReq.getNik()).getUser();

        if (!user.hasRole("admin")) {
            if (!user.canLogin()) {
                throw new AccessDeniedException("auth.group.disable.login");
            }
        }

        boolean hasPassword = user.getCredential().getPassword() != null;
        if (!hasPassword) {
            if (!authReq.isConfirmed()) {
                return AuthResDTO.builder()
                        .code(PASSWORD_CONFIRMATION)
                        .build();
            }
            else {
                auditProvider.setName(user.getName());
                user.getCredential().setPassword(passwordEncoder.encode(authReq.getPassword()));
                user.setCredential(userCredentialRepo.save(user.getCredential()));
            }
        }
        else {
            boolean passwordMatch = passwordEncoder.matches(authReq.getPassword(), user.getCredential().getPassword());
            if (!passwordMatch) {
                throw new UnauthorizedException("auth.user.invalid.password");
            }
        }

        JwtResult accessToken = JwtUtil.accessToken(user, application);
        JwtResult refreshToken = JwtUtil.refreshToken(user, application);

        user.setCredential(userCredentialRepo.save(user.getCredential()));
        return AuthResDTO.builder()
                .code(SUCCESS)
                .user(user)
                .accessToken(accessToken.getToken())
                .expiredAt(accessToken.getExpiredAt().getEpochSecond())
                .refreshToken(refreshToken.getToken())
                .refreshExpiredAt(refreshToken.getExpiredAt().getEpochSecond())
                .build();
    }

    @Override
    public AuthResDTO refresh(String refreshToken) {
        try {
            JwtToken decode = JwtUtil.decode(refreshToken);
            String audience = decode.getAudience();
            User user = userRepo.findById(decode.getUserId())
                    .orElseThrow();

            JwtResult jwtAccessToken = JwtUtil.accessToken(user, audience);
            JwtResult jwtRefreshToken = JwtUtil.refreshToken(user, audience);
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

}

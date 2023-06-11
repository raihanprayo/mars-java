package dev.scaraz.mars.user.service;

import dev.scaraz.mars.common.domain.request.AuthReqDTO;
import dev.scaraz.mars.common.domain.response.AuthResDTO;
import dev.scaraz.mars.common.exception.web.UnauthorizedException;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.security.MarsDatasourceAuditor;
import dev.scaraz.mars.security.jwt.JwtAccessToken;
import dev.scaraz.mars.security.jwt.JwtUtil;
import dev.scaraz.mars.user.domain.MarsUser;
import dev.scaraz.mars.user.query.UserQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

import static dev.scaraz.mars.common.utils.AppConstants.Auth.PASSWORD_CONFIRMATION;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserService userService;
    private final UserQueryService userQueryService;

    private final PasswordEncoder passwordEncoder;
    private final MarsDatasourceAuditor datasourceAuditor;

    public AuthResDTO authenticate(AuthReqDTO authReq) {
        MarsUser user = (MarsUser) userQueryService.loadUserByUsername(authReq.getNik());

        boolean hasPassword = user.getPassword() != null;
        if (!hasPassword) {
            if (!authReq.isConfirmed()) {
                return AuthResDTO.builder()
                        .code(PASSWORD_CONFIRMATION)
                        .build();
            }
            else {
                datasourceAuditor.setUsername(user.getNik());
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
            else if (!user.isEnabled())
                throw new UnauthorizedException("Silahkan menghubungi tim admin untuk mengaktifkan akunmu");
        }

        Instant issuedAt = Instant.now();
        JwtAccessToken accessToken = JwtAccessToken.builder()
                .aud("web")
                .subject(user.getId())
                .nik(user.getNik())
                .telegram(user.getTelegram())
                .witel(user.getWitel())
                .sto(user.getSto())
                .roles(user.getRoles())
                .issuedAt(Date.from(issuedAt))
                .build();
        String token = JwtUtil.encode(accessToken);

        accessToken.setRefreshToken(true);
        String refreshToken = JwtUtil.encode(accessToken);
        return AuthResDTO.builder()
                .code(AppConstants.Auth.SUCCESS)
                .accessToken(token)
                .refreshToken(refreshToken)
                .build();
    }

}

package dev.scaraz.mars.core.web.rest;

import dev.scaraz.mars.common.config.properties.MarsProperties;
import dev.scaraz.mars.common.domain.request.AuthReqDTO;
import dev.scaraz.mars.common.domain.request.RefreshTokenReqDTO;
import dev.scaraz.mars.common.domain.response.AuthResDTO;
import dev.scaraz.mars.common.domain.response.JwtToken;
import dev.scaraz.mars.common.domain.response.WhoamiDTO;
import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.exception.web.UnauthorizedException;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.core.config.security.JwtUtil;
import dev.scaraz.mars.core.domain.credential.Roles;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.mapper.CredentialMapper;
import dev.scaraz.mars.core.repository.credential.RolesRepo;
import dev.scaraz.mars.core.service.AuthService;
import dev.scaraz.mars.core.util.SecurityUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthResource {

    private final MarsProperties marsProperties;
    private final AuthService authService;
    private final CredentialMapper credentialMapper;

    @GetMapping("/whoami")
    public ResponseEntity<?> whoami() {
        User user = SecurityUtil.getCurrentUser();
        return ResponseEntity.ok(credentialMapper.fromUser(user));
    }

    @PostMapping(value = "/token")
    public ResponseEntity<?> token(@RequestBody AuthReqDTO authReq) {
        AuthResDTO authResult = authService.authenticate(authReq, "mars-dashboard");
        if (!authResult.getCode().equals(AppConstants.Auth.SUCCESS)) {
            return ResponseEntity
                    .status(400)
                    .body(authResult);
        }
        return ResponseEntity.ok(authResult);
    }

    @GetMapping("/authorize")
    public ResponseEntity<?> authorize(@RequestHeader("Authorization") String bearer) {
        try {
            String token = bearer.substring("Bearer ".length());
            JwtUtil.decode(token);
            return new ResponseEntity<>(
                    Map.of("ok", true),
                    HttpStatus.OK);
        }
        catch (ExpiredJwtException ex) {
            return new ResponseEntity<>(
                    Map.of("ok", false,
                            "code", "refresh-required"),
                    HttpStatus.BAD_REQUEST
            );
        }
        catch (Exception ex) {
            throw BadRequestException.args(ex.getMessage());
        }
    }

    @PostMapping("/unauthorize")
    public ResponseEntity<?> unauthorize() {

        ResponseCookie cookieToken = ResponseCookie
                .from(AppConstants.Auth.COOKIE_TOKEN, "")
                .maxAge(0)
                .build();

        ResponseCookie cookieRefreshToken = ResponseCookie
                .from(AppConstants.Auth.COOKIE_REFRESH_TOKEN, "")
                .maxAge(0)
                .build();

        return ResponseEntity
                .status(200)
                .header("Set-Cookie", cookieToken.toString())
                .header("Set-Cookie", cookieRefreshToken.toString())
                .build();
    }

    @PostMapping(path = "/refresh")
    public ResponseEntity<?> refresh(
            HttpServletRequest request,
            @RequestBody(required = false) RefreshTokenReqDTO req
    ) {
        AuthResDTO authResult = authService.refresh(req.getRefreshToken());
        return ResponseEntity.ok(authResult);
    }

    private ResponseEntity<?> attachJwtCookie(AuthResDTO authResult) {
        long issuedAt = authResult.getIssuedAt();
        long expiredAt = authResult.getExpiredAt();
        long refreshExpiredAt = authResult.getRefreshExpiredAt();
        ResponseCookie cookieToken = ResponseCookie.from(AppConstants.Auth.COOKIE_TOKEN, authResult.getAccessToken())
                .httpOnly(marsProperties.getCookie().isHttpOnly())
                .domain(marsProperties.getCookie().getDomain())
                .path(marsProperties.getCookie().getPath())
                .secure(marsProperties.getCookie().isSecure())
                .maxAge(expiredAt - issuedAt)
                .build();

        ResponseCookie cookieRefreshToken = ResponseCookie.from(AppConstants.Auth.COOKIE_REFRESH_TOKEN, authResult.getAccessToken())
                .httpOnly(marsProperties.getCookie().isHttpOnly())
                .domain(marsProperties.getCookie().getDomain())
                .path(marsProperties.getCookie().getPath())
                .secure(marsProperties.getCookie().isSecure())
                .maxAge(refreshExpiredAt - issuedAt)
                .build();

        return ResponseEntity
                .status(200)
                .header("Set-Cookie", cookieToken.toString())
                .header("Set-Cookie", cookieRefreshToken.toString())
                .body(authResult);
    }

}

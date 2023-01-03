package dev.scaraz.mars.core.web.rest;

import dev.scaraz.mars.common.config.properties.MarsProperties;
import dev.scaraz.mars.common.domain.request.AuthReqDTO;
import dev.scaraz.mars.common.domain.response.AuthResDTO;
import dev.scaraz.mars.common.domain.response.WhoamiDTO;
import dev.scaraz.mars.common.exception.web.UnauthorizedException;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.core.domain.credential.Roles;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.mapper.UserMapper;
import dev.scaraz.mars.core.query.RoleQueryService;
import dev.scaraz.mars.core.query.criteria.RoleCriteria;
import dev.scaraz.mars.core.repository.credential.RolesRepo;
import dev.scaraz.mars.core.service.credential.UserAuthService;
import dev.scaraz.mars.core.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthResource {

    private final MarsProperties marsProperties;
    private final UserAuthService authService;
    private final UserMapper userMapper;
    private final RolesRepo rolesRepo;

    @GetMapping("/whoami")
    public ResponseEntity<?> whoami() {
        User user = SecurityUtil.getCurrentUser();
        WhoamiDTO whoamiDTO = userMapper.fromUser(user);

        List<Roles> roles = rolesRepo.findAllByUserId(user.getId());
        for (Roles map : roles) {
            if (map.getRole().isGroupRole()) whoamiDTO.getGroup().getRoles().add(map.getRole().getName());
            else whoamiDTO.getRoles().add(map.getRole().getName());
        }

        return ResponseEntity.ok(whoamiDTO);
    }

    @PostMapping(value = "/authorize")
    public ResponseEntity<?> authorize(@RequestBody AuthReqDTO authReq) {
        AuthResDTO authResult = authService.authenticate(authReq, "mars-dashboard");
        if (!Objects.equals(authResult.getCode(), AppConstants.Auth.SUCCESS)) {
            return ResponseEntity
                    .status(400)
                    .body(authResult);
        }

        return attachJwtCookie(authResult);
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

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, @RequestBody(required = false) String refreshToken) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (!cookie.getName().equals(AppConstants.Auth.COOKIE_REFRESH_TOKEN))
                    continue;

                AuthResDTO authResult = authService.refresh(cookie.getValue());
                return attachJwtCookie(authResult);
            }
        }
        else {
            AuthResDTO authResult = authService.refresh(refreshToken);
            return attachJwtCookie(authResult);
        }

        throw new UnauthorizedException("invalid refresh token");
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

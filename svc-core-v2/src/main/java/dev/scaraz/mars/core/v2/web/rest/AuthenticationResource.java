package dev.scaraz.mars.core.v2.web.rest;

import dev.scaraz.mars.common.domain.request.AuthReqDTO;
import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.exception.web.UnauthorizedException;
import dev.scaraz.mars.core.v2.service.credential.AuthService;
import dev.scaraz.mars.security.authentication.MarsJwtAuthenticationToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor

@RestController
@RequestMapping("/auth")
public class AuthenticationResource {

    private final AuthService authService;

    @GetMapping("/whoami")
    public ResponseEntity<?> whoami(Authentication authentication) {
        if (authentication instanceof MarsJwtAuthenticationToken) {
            MarsJwtAuthenticationToken jwtAuth = (MarsJwtAuthenticationToken) authentication;
            return new ResponseEntity<>(jwtAuth.getPrincipal(), HttpStatus.OK);
        }

        throw new UnauthorizedException("Invalid authentication");
    }

    @PostMapping(
            path = "/token",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> token(@RequestBody AuthReqDTO authReq) {
        log.info("NEW TOKEN REQUEST");
        return new ResponseEntity<>(authService.token(authReq), HttpStatus.OK);
    }

    @PostMapping(
            path = "/refresh",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> refreshToken(Authentication authentication) {
        boolean isMarsJwtAuth = authentication instanceof MarsJwtAuthenticationToken;
        if (!isMarsJwtAuth)
            throw new IllegalStateException("invalid authentication token");

        MarsJwtAuthenticationToken marsJwtAuth = (MarsJwtAuthenticationToken) authentication;
        if (!marsJwtAuth.isRefreshToken())
            throw new IllegalStateException("format JWT tidak sesuai");

        return ResponseEntity.ok(authService.refresh(marsJwtAuth));
    }

}

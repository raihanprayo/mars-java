package dev.scaraz.mars.user.web.rest;

import dev.scaraz.mars.common.domain.request.AuthRefreshDTO;
import dev.scaraz.mars.common.domain.request.AuthReqDTO;
import dev.scaraz.mars.common.domain.response.AuthResDTO;
import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.security.jwt.JwtAccessToken;
import dev.scaraz.mars.security.jwt.JwtUtil;
import dev.scaraz.mars.user.service.AuthService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthResource {

    private final AuthService authService;

    @PostMapping(
            path = "/login",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> login(
            @ModelAttribute @Valid AuthReqDTO authReq
    ) {
        AuthResDTO authResult = authService.webAuthentication(authReq);
        if (!authResult.getCode().equals(AppConstants.Auth.SUCCESS)) {
            return ResponseEntity
                    .status(400)
                    .body(authResult);
        }
        return new ResponseEntity<>(authResult, HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> register(@RequestBody @Valid AuthRefreshDTO req) {
        try {
            JwtAccessToken token = JwtUtil.decode(req.getRefreshToken());
            if (!token.isRefreshToken())
                throw new BadRequestException("invalid refresh token");

            AuthResDTO authResult = authService.webRefreshAuthentication(token);
            return new ResponseEntity<>(authResult, HttpStatus.OK);
        }
        catch (ExpiredJwtException ex) {
            throw new BadRequestException("refresh token expired");
        }
        catch (MalformedJwtException | UnsupportedJwtException | SignatureException | IllegalArgumentException ex) {
            throw new BadRequestException("invalid refresh token");
        }
    }

}

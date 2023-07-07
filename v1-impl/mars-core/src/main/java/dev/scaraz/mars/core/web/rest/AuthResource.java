package dev.scaraz.mars.core.web.rest;

import dev.scaraz.mars.common.config.properties.MarsProperties;
import dev.scaraz.mars.common.domain.request.AuthReqDTO;
import dev.scaraz.mars.common.domain.request.ForgotReqDTO;
import dev.scaraz.mars.common.domain.response.AuthResDTO;
import dev.scaraz.mars.common.domain.response.ForgotResDTO;
import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.mapper.CredentialMapper;
import dev.scaraz.mars.core.query.UserQueryService;
import dev.scaraz.mars.core.service.AuthService;
import dev.scaraz.mars.core.util.SecurityUtil;
import dev.scaraz.mars.security.authentication.MarsJwtAuthenticationToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthResource {

    private final MarsProperties marsProperties;
    private final AuthService authService;
    private final UserQueryService userQueryService;
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

    @PostMapping(path = "/refresh", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> refresh(Authentication authentication) {
        if (!(authentication instanceof MarsJwtAuthenticationToken))
            throw new IllegalStateException("invalid authentication token");

        MarsJwtAuthenticationToken token = (MarsJwtAuthenticationToken) authentication;
        if (!token.isRefreshToken())
            throw new IllegalStateException("format JWT tidak sesuai");

        AuthResDTO authResult = authService.refresh(token);
        return ResponseEntity.ok(authResult);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestParam(defaultValue = "false") boolean confirmeLogout
    ) {
        authService.logout(SecurityUtil.getCurrentUser(), confirmeLogout);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RestController
    @RequestMapping("/auth/forgot")
    public class AuthForgotResource {

        @GetMapping
        public ResponseEntity<?> forgotAccess(@RequestParam("u") String username) {
            try {
                User user = userQueryService.loadUserByUsername(username);
                boolean accessibleViaEmail = StringUtils.isNoneBlank(user.getEmail());
                boolean accessibleViaTelegram = user.getTg().getId() != null;
                return ResponseEntity.ok(Map.of(
                        "email", accessibleViaEmail,
                        "telegram", accessibleViaTelegram
                ));
            }
            catch (UsernameNotFoundException ex) {
                throw new BadRequestException("User not found");
            }
        }

        @PostMapping("/generate")
        public ResponseEntity<?> generateOtp(@RequestBody ForgotReqDTO req) {
            req.setState(ForgotReqDTO.State.GENERATE);
            return ResponseEntity.ok(authService.forgotPasswordFlow(req));
        }

        @PutMapping("/regenerate")
        public ResponseEntity<?> regenerateOtp(@RequestParam String token) {
            return new ResponseEntity<>(
                    authService.forgotRegenerateOtp(token),
                    HttpStatus.OK
            );
        }

        @PostMapping("/validate")
        public ResponseEntity<?> validateOtp(@RequestBody ForgotReqDTO forgot) {
            forgot.setState(ForgotReqDTO.State.VALIDATE);
            return new ResponseEntity<>(authService.forgotPasswordFlow(forgot), HttpStatus.OK);
        }

        @PutMapping("/reset")
        public ResponseEntity<?> resetAccount(@RequestBody ForgotReqDTO forgot) {
            forgot.setState(ForgotReqDTO.State.ACCOUNT_RESET);
            return new ResponseEntity<>(authService.forgotPasswordFlow(forgot), HttpStatus.OK);
        }

    }
}

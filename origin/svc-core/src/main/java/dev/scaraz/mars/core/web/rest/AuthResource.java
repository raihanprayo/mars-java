package dev.scaraz.mars.core.web.rest;

import dev.scaraz.mars.common.config.properties.MarsProperties;
import dev.scaraz.mars.common.domain.request.AuthReqDTO;
import dev.scaraz.mars.common.domain.request.ForgotReqDTO;
import dev.scaraz.mars.common.domain.response.AuthResDTO;
import dev.scaraz.mars.common.domain.response.ForgotResDTO;
import dev.scaraz.mars.common.domain.response.WhoamiDTO;
import dev.scaraz.mars.common.exception.web.BadRequestException;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.mapper.CredentialMapper;
import dev.scaraz.mars.core.query.AccountQueryService;
import dev.scaraz.mars.core.service.AuthService;
import dev.scaraz.mars.core.service.ConfigService;
import dev.scaraz.mars.security.authentication.token.MarsWebAuthenticationToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthResource {

    private final MarsProperties marsProperties;
    private final ConfigService configService;
    private final AuthService authService;
    private final AccountQueryService accountQueryService;
    private final CredentialMapper credentialMapper;

    @GetMapping("/whoami")
    public ResponseEntity<WhoamiDTO> whoami() {
        return ResponseEntity.ok(credentialMapper.fromUser(accountQueryService.findByCurrentAccess()));
    }

    @PostMapping(value = "/token")
    public ResponseEntity<AuthResDTO> token(
            HttpServletRequest request,
            @RequestBody AuthReqDTO authReq) {
        AuthResDTO authResult = authService.authenticate(request, authReq, "mars-dashboard");
        if (!authResult.getCode().equals(AppConstants.Auth.SUCCESS)) {
            return ResponseEntity
                    .status(400)
                    .body(authResult);
        }

        return ResponseEntity.ok(authResult);
    }

    @PostMapping(path = "/refresh", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResDTO> refresh(Authentication authentication) {
        if (!(authentication instanceof MarsWebAuthenticationToken))
            throw new IllegalStateException("invalid authentication token");

        MarsWebAuthenticationToken token = (MarsWebAuthenticationToken) authentication;
        if (!token.isRefreshToken())
            throw new IllegalStateException("format JWT tidak sesuai");

        AuthResDTO authResult = authService.refresh(token);
        return ResponseEntity.ok(authResult);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            @RequestParam(defaultValue = "false") boolean confirmeLogout
    ) {
        authService.logout(request, accountQueryService.findByCurrentAccess(), confirmeLogout);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RestController
    @RequestMapping("/auth/forgot")
    public class AuthForgotResource {

        @GetMapping
        public ResponseEntity<Map<String, Boolean>> forgotAccess(@RequestParam("u") String username) {
            try {
                Account account = accountQueryService.loadUserByUsername(username);
                boolean accessibleViaEmail = StringUtils.isNoneBlank(account.getEmail());
//                boolean accessibleViaTelegram = account.getTg().getId() != null;
                boolean accessibleViaTelegram = Optional.ofNullable(account.getTg())
                        .map(tg -> tg.getId() != null)
                        .orElse(false);
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
        public ResponseEntity<ForgotResDTO> generateOtp(@RequestBody ForgotReqDTO req) {
            req.setState(ForgotReqDTO.State.GENERATE);
            return ResponseEntity.ok(authService.forgotPasswordFlow(req));
        }

        @PutMapping("/regenerate")
        public ResponseEntity<ForgotResDTO> regenerateOtp(@RequestParam String token) {
            return new ResponseEntity<>(
                    authService.forgotRegenerateOtp(token),
                    HttpStatus.OK
            );
        }

        @PostMapping("/validate")
        public ResponseEntity<ForgotResDTO> validateOtp(@RequestBody ForgotReqDTO forgot) {
            forgot.setState(ForgotReqDTO.State.VALIDATE);
            return new ResponseEntity<>(authService.forgotPasswordFlow(forgot), HttpStatus.OK);
        }

        @PutMapping("/reset")
        public ResponseEntity<ForgotResDTO> resetAccount(@RequestBody ForgotReqDTO forgot) {
            forgot.setState(ForgotReqDTO.State.ACCOUNT_RESET);
            return new ResponseEntity<>(authService.forgotPasswordFlow(forgot), HttpStatus.OK);
        }

    }
}

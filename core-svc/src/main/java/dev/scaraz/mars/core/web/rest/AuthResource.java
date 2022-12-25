package dev.scaraz.mars.core.web.rest;

import dev.scaraz.mars.common.domain.request.AuthReqDTO;
import dev.scaraz.mars.common.domain.response.AuthResDTO;
import dev.scaraz.mars.core.service.credential.UserAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthResource {

    private final UserAuthService authService;

    @GetMapping("/whoami")
    public ResponseEntity<?> whoami() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/authorize")
    public ResponseEntity<?> authorize(@RequestBody AuthReqDTO authReq) {
        AuthResDTO authResult = authService.login(authReq, "mars-dashboard");
        return ResponseEntity.ok(authResult);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody String refreshToken) {
        AuthResDTO authResult = authService.refresh(refreshToken);
        return ResponseEntity.ok(authResult);
    }

}

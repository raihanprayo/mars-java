package dev.scaraz.mars.core.v2.web.rest;

import dev.scaraz.mars.common.domain.request.AuthReqDTO;
import dev.scaraz.mars.core.v2.service.credential.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor

@RestController
@RequestMapping("/auth")
public class AuthenticationResource {

    private final AuthService authService;

    @PostMapping(
            path = "/token",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> token(@RequestBody AuthReqDTO authReq) {
        log.info("NEW TOKEN REQUEST");
        return new ResponseEntity<>(authService.token(authReq), HttpStatus.OK);
    }

}

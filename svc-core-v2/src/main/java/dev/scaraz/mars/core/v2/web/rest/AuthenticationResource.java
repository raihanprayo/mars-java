package dev.scaraz.mars.core.v2.web.rest;

import dev.scaraz.mars.common.domain.request.AuthReqDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationResource {

    @PostMapping("/token")
    public ResponseEntity<?> token(@RequestBody AuthReqDTO authReq) {
        Instant now = Instant.now();
        return null;
    }

}

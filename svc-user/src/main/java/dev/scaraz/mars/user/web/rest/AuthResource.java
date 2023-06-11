package dev.scaraz.mars.user.web.rest;

import dev.scaraz.mars.common.domain.request.AuthReqDTO;
import dev.scaraz.mars.common.domain.response.AuthResDTO;
import dev.scaraz.mars.common.utils.AppConstants;
import dev.scaraz.mars.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthResource {

    private final AuthService authService;

    @PostMapping(
            path = "/login",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> login(
            @RequestBody AuthReqDTO authReq
    ) {
        AuthResDTO authResult = authService.authenticate(authReq);
        if (!authResult.getCode().equals(AppConstants.Auth.SUCCESS)) {
            return ResponseEntity
                    .status(400)
                    .body(authResult);
        }
        return new ResponseEntity<>(authResult, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register() {
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}

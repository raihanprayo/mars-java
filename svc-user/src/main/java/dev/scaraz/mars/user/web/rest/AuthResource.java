package dev.scaraz.mars.user.web.rest;

import dev.scaraz.mars.common.domain.request.AuthReqDTO;
import dev.scaraz.mars.common.exception.web.UnauthorizedException;
import dev.scaraz.mars.common.tools.security.JwtProvider;
import dev.scaraz.mars.user.datasource.domain.Sto;
import dev.scaraz.mars.user.datasource.domain.User;
import dev.scaraz.mars.user.service.app.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor

@RestController
@RequestMapping("/auth")
public class AuthResource {

    private final JwtProvider jwtProvider;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @PostMapping(path = "/login")
    public ResponseEntity<?> login(
            @RequestBody @Valid AuthReqDTO login
    ) {
        User user = (User) userService.loadUserByUsername(login.getUsername());

        userService.check(user);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                login.getUsername(),
                login.getPassword(),
                user.getAuthorities());
        Authentication authenticate = authenticationManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(authenticate);

        String token = jwtProvider.generate(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getEmail(),
                user.getInfo().getTgId(),
                user.getInfo().getWitel(),
                Optional.ofNullable(user.getInfo().getSto())
                        .map(Sto::getAlias)
                        .orElse(null),
                user.getAuthorities(),
                3_600_000
        );

        return ResponseEntity.ok(Map.of(
                "type", "Bearer",
                "access_token", token
        ));
    }

    @GetMapping(
            path = "/claims",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> claims(
            @RequestHeader(name = "Authorization", required = false) String bearer
    ) {
        if (StringUtils.isBlank(bearer))
            throw new UnauthorizedException();

        String token = bearer.substring("Bearer ".length());

        try {
            Claims body = jwtProvider.decode(token)
                    .getBody();

            return ResponseEntity.ok(jwtProvider.parse(body));
        }
        catch (Exception ex) {
            ex.printStackTrace();
            throw new UnauthorizedException(ex.getMessage());
        }
    }

}

package dev.scaraz.mars.core.config.security;


import dev.scaraz.mars.common.config.properties.MarsProperties;
import dev.scaraz.mars.common.domain.response.JwtResult;
import dev.scaraz.mars.common.domain.response.JwtToken;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.domain.credential.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j

@Component
public class JwtUtil {
    private final static AtomicReference<SecretKey> secret = new AtomicReference<>();

    public JwtUtil(MarsProperties marsProperties) {
        secret.set(Keys.hmacShaKeyFor(marsProperties
                .getSecret()
                .getBytes(StandardCharsets.UTF_8)));
    }

    public static JwtResult accessToken(User user, String application) {
        return generate(user, application, 12, ChronoUnit.HOURS, false);
    }

    public static JwtResult refreshToken(User user, String application) {
        return generate(user, application, 48, ChronoUnit.HOURS, true);
    }

    public static JwtResult generate(User user, String application, long expiredIn, ChronoUnit timeUnit, boolean asRefreshToken) {
        Map<String, Object> group = Map.of(
                "id", user.getGroup().getId(),
                "name", user.getGroup().getName()
        );

        Instant now = Instant.now();
        Claims claims = new DefaultClaims();
        claims.setId(UUID.randomUUID().toString());
        claims.setAudience(application);
        claims.setSubject(user.getId());
        claims.setIssuedAt(Date.from(now));
        claims.setExpiration(Date.from(now.plus(expiredIn, timeUnit)));

        claims.put("rfs", asRefreshToken);
        claims.put("name", user.getName());
        claims.put("tg", user.getTelegramId());
        claims.put("group", group);
        claims.put("roles", user.getRoles().stream()
                .filter(r -> !r.isGroupRole())
                .map(Role::getName)
                .collect(Collectors.toList()));

        return JwtResult.builder()
                .id(claims.getId())
                .token(Jwts.builder()
                        .setClaims(claims)
                        .signWith(secret.get())
                        .compact())
                .expiredAt(claims.getExpiration().toInstant())
                .build();
    }

    public static JwtToken decode(String token) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException,
            IllegalArgumentException {
        Jws<Claims> jws = Jwts.parserBuilder()
                .setSigningKey(secret.get())
                .build()
                .parseClaimsJws(token);

        Claims decode = jws.getBody();

        List<String> roles = (List<String>) decode.get("roles");
        Map<String, Object> groupDecoded = (Map<String, Object>) decode.get("group");

        JwtToken.JwtGroupToken group = JwtToken.JwtGroupToken.builder()
                .id((String) groupDecoded.get("id"))
                .name((String) groupDecoded.get("name"))
                .build();

        return JwtToken.builder()
                .id(decode.getId())
                .audience(decode.getAudience())
                .refresher(decode.get("rfs", Boolean.class))
                .userId(decode.getSubject())
                .issuedAt(decode.getIssuedAt().toInstant())
                .expiredAt(decode.getExpiration().toInstant())
                .name(decode.get("name", String.class))
                .telegram(decode.get("tg", Long.class))
                .roles(roles)
                .group(group)
                .build();
    }

    public static boolean validate(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secret.get())
                    .build()
                    .parseClaimsJws(token);
            return true;
        }
        catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException |
               IllegalArgumentException ex) {
            return false;
        }
    }

}

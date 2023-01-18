package dev.scaraz.mars.core.config.security;


import dev.scaraz.mars.common.config.properties.MarsProperties;
import dev.scaraz.mars.common.domain.response.JwtResult;
import dev.scaraz.mars.common.domain.response.JwtToken;
import dev.scaraz.mars.common.tools.enums.Witel;
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

    public static JwtResult accessToken(User user, String application, Instant issuedAt) {
        return generate(user, application, 12, ChronoUnit.HOURS, issuedAt, false);
    }

    public static JwtResult refreshToken(User user, String application, Instant issuedAt) {
        return generate(user, application, 48, ChronoUnit.HOURS, issuedAt, true);
    }

    public static JwtResult generate(User user,
                                     String application,
                                     long expiredIn,
                                     ChronoUnit timeUnit,
                                     Instant issuedAt,
                                     boolean asRefreshToken) {

        Claims claims = new DefaultClaims();
        claims.setId(UUID.randomUUID().toString());
        claims.setAudience(application);
        claims.setSubject(user.getId());
        claims.setIssuedAt(Date.from(issuedAt));
        claims.setExpiration(Date.from(issuedAt.plus(expiredIn, timeUnit)));

        if (asRefreshToken) claims.put("rfs", true);

        claims.put("name", user.getName());
        claims.put("tg", user.getTelegramId());
        claims.put("witel", user.getWitel());
        claims.put("sto", user.getSto());
        claims.put("roles", user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList()));

        if (user.getGroup() != null) {
            Map<String, Object> group = Map.of(
                    "id", user.getGroup().getId(),
                    "name", user.getGroup().getName()
            );
            claims.put("group", group);
        }

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

        boolean refresher = false;
        try {
            refresher = decode.get("rfs", Boolean.class);
        }
        catch (Exception ex) {
        }

        JwtToken.JwtTokenBuilder jwt = JwtToken.builder()
                .id(decode.getId())
                .audience(decode.getAudience())
                .refresher(refresher)
                .userId(decode.getSubject())
                .issuedAt(decode.getIssuedAt().toInstant())
                .expiredAt(decode.getExpiration().toInstant())
                .name(decode.get("name", String.class))
                .telegram(decode.get("tg", Long.class))
                .witel(Witel.valueOf(decode.get("witel", String.class)))
                .sto(decode.get("sto", String.class))
                .roles(roles);

        Map<String, Object> groupDecoded = (Map<String, Object>) decode.get("group");
        if (groupDecoded != null) {
            JwtToken.JwtGroupToken group = JwtToken.JwtGroupToken.builder()
                    .id((String) groupDecoded.get("id"))
                    .name((String) groupDecoded.get("name"))
                    .build();
            jwt.group(group);
        }

        return jwt.build();
    }

}

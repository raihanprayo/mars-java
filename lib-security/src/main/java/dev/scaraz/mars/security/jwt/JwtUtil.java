package dev.scaraz.mars.security.jwt;


import dev.scaraz.mars.common.tools.enums.Witel;
import dev.scaraz.mars.security.MarsSecurityProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class JwtUtil {

    private static JwtUtil INSTANCE;

    private final SecretKey secret;
    private final MarsSecurityProperties securityProperties;

    public JwtUtil(MarsSecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
        this.secret = (Keys.hmacShaKeyFor(securityProperties
                .getJwt()
                .getSecret()
                .getBytes()));

        INSTANCE = this;
    }

    public static String encode(JwtAccessToken accessToken) {
        Objects.requireNonNull(accessToken.getIssuedAt(), "issuedAt cannot be empty");
        Instant issuedAt = accessToken.getIssuedAt().toInstant();

        Claims claims = new DefaultClaims();
        claims.setSubject(accessToken.getSubject());
        claims.setAudience(accessToken.getAud());
        claims.setIssuedAt(Date.from(issuedAt));
        claims.setExpiration(Date.from(issuedAt.plus(
                INSTANCE.securityProperties.getJwt().getTokenDuration().toMillis(),
                ChronoUnit.MILLIS
        )));

        claims.put("tg", accessToken.getWitel());
        claims.put("witel", accessToken.getWitel());
        claims.put("sto", accessToken.getSto());
        claims.put("roles", accessToken.getRoles().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toUnmodifiableList()));

        if (!accessToken.isRefreshToken()) {
            claims.put("tip", "acs");
        }
        else {
            claims.put("tip", "rfs");
            claims.setExpiration(Date.from(issuedAt.plus(
                    INSTANCE.securityProperties.getJwt().getRefreshTokenDuration().toMillis(),
                    ChronoUnit.MILLIS
            )));
        }

        return Jwts.builder()
                .setClaims(claims)
                .signWith(INSTANCE.secret)
                .compact();
    }

    public static JwtAccessToken decode(String token) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {
        Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(INSTANCE.secret)
                .build()
                .parseClaimsJws(token);

        Claims body = claims.getBody();
        Witel witel = Witel.valueOf(body.get("witel", String.class));
        List<SimpleGrantedAuthority> roles = new ArrayList<>();

        List<String> rawRoles = body.get("roles", List.class);
        rawRoles.stream()
                .map(SimpleGrantedAuthority::new)
                .forEach(roles::add);

        boolean isRefreshToken = body.get("tip", String.class).equals("rfs");

        return JwtAccessToken.builder()
                .aud(body.getAudience())
                .subject(body.getSubject())
                .nik(body.get("nik", String.class))
                .telegram(body.get("tg", Long.class))
                .witel(witel)
                .sto(body.get("sto", String.class))
                .roles(roles)
                .expiredAt(body.getExpiration())
                .issuedAt(body.getIssuedAt())
                .refreshToken(isRefreshToken)
                .build();
    }

}

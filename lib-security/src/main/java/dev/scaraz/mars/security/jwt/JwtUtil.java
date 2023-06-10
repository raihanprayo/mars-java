package dev.scaraz.mars.security.jwt;


import dev.scaraz.mars.common.tools.enums.Witel;
import dev.scaraz.mars.security.MarsSecurityProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
        Instant issuedAt = Instant.now();

        Claims claims = new DefaultClaims();
        claims.setSubject(accessToken.getSubject());
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

        return Jwts.builder()
                .setClaims(claims)
                .signWith(INSTANCE.secret)
                .compact();
    }

    public static JwtAccessToken decode(String token) {
        Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(INSTANCE.secret)
                .build()
                .parseClaimsJws(token);

        Witel witel = Witel.valueOf(claims.getBody().get("witel", String.class));
        List<SimpleGrantedAuthority> roles = new ArrayList<>();

        List<String> rawRoles = claims.getBody().get("roles", List.class);
        rawRoles.stream()
                .map(SimpleGrantedAuthority::new)
                .forEach(roles::add);

        return JwtAccessToken.builder()
                .subject(claims.getBody().getSubject())
                .nik(claims.getBody().get("nik", String.class))
                .telegram(claims.getBody().get("tg", Long.class))
                .witel(witel)
                .sto(claims.getBody().get("sto", String.class))
                .roles(roles)
                .build();
    }

}

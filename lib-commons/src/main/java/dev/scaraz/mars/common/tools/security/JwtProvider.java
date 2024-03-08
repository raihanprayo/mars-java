package dev.scaraz.mars.common.tools.security;

import dev.scaraz.mars.common.config.properties.MarsProperties;
import dev.scaraz.mars.common.domain.general.AccessToken;
import dev.scaraz.mars.common.tools.enums.Witel;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ClaimsBuilder;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.GrantedAuthority;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JwtProvider {

    private final MarsProperties properties;

    public String generate(
            String userId,
            String nik,
            String name,
            String email,
            long tgId,
            Witel witel,
            String sto,
            Collection<? extends GrantedAuthority> authorities,
            long expired
    ) {
        Date currentDate = new Date();
        SecretKey secretKey = Keys.hmacShaKeyFor(properties
                .getSecret()
                .getBytes(StandardCharsets.UTF_8));


//        Claims claims = new DefaultClaims();
//        claims.setId(UUID.randomUUID().toString());
//        claims.setSubject(userId);
//        claims.setIssuedAt(currentDate);
//        claims.setExpiration(new Date(currentDate.getTime() + expired));
//
//        claims.put("name", name);
//        claims.put("email", email);
//        claims.put("nik", nik);
//        claims.put("tg", tgId);
//
//        TreeMap<String, Object> info = new TreeMap<>();
//        info.put("witel", witel);
//        if (sto != null) info.put("sto", sto);
//
//        claims.put("info", info);
//        claims.put("roles", authorities.stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.toList()));

        ClaimsBuilder cb = Jwts.claims()
                .id(UUID.randomUUID().toString())
                .subject(userId)
                .issuedAt(currentDate)
                .expiration(new Date(currentDate.getTime() + expired))
                .add("name", name)
                .add("email", email)
                .add("nik", nik)
                .add("tg", tgId);

        TreeMap<String, Object> info = new TreeMap<>();
        info.put("witel", witel);
        if (sto != null) info.put("sto", sto);

        cb.add("info", info);
        cb.add("roles", authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return Jwts.builder()
                .claims(cb.build())
                .signWith(secretKey)
                .compact();
    }

    public void validate(String token) {
        try {
            decode(token);
        }
        catch (Exception ex) {
            throw new AuthenticationCredentialsNotFoundException("Invalid or Expired JWT token");
        }
    }

    public Jws<Claims> decode(String token) {
        SecretKey secretKey = Keys.hmacShaKeyFor(properties
                .getSecret()
                .getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
    }

    public AccessToken parse(Claims claims) {
        Map<String, Object> info = claims.get("info", Map.class);
        AccessToken.AccessInfo accessInfo = AccessToken.AccessInfo.builder()
                .witel(Witel.valueOf((String) info.get("witel")))
                .sto((String) info.get("sto"))
                .build();

        List<String> roles = claims.get("roles", List.class);
        return AccessToken.builder()
                .id(claims.getId())
                .subject(claims.getSubject())
                .name(claims.get("name", String.class))
                .email(claims.get("email", String.class))
                .nik(claims.get("nik", String.class))
                .tg(claims.get("tg", Long.class))
                .info(accessInfo)
                .issued(claims.getIssuedAt())
                .expired(claims.getExpiration())
                .roles(new HashSet<>(roles))
                .build();
    }

}

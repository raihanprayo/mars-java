package dev.scaraz.mars.security.jwt;

import dev.scaraz.mars.common.domain.response.JwtResult;
import dev.scaraz.mars.common.tools.enums.Witel;
import dev.scaraz.mars.security.authentication.identity.MarsAccessToken;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.Assert;

import java.security.Key;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class JwtUtil {
    private static final Duration DEFAULT_EXPIRED_ACS = Duration.ofHours(1);
    private static final Duration DEFAULT_EXPIRED_RFS = Duration.ofHours(12);

    private static final List<String> ISSUERS = List.of(
            MarsAccessToken.ISSUER_WEB,
            MarsAccessToken.ISSUER_API);
    private static final List<String> AUDIENCES = List.of(
            MarsAccessToken.ACS,
            MarsAccessToken.RFS);

    private static final AtomicReference<Key> secret = new AtomicReference<>();
    private static final AtomicReference<Supplier<Duration>> accessExpired = new AtomicReference<>(() -> DEFAULT_EXPIRED_ACS);
    private static final AtomicReference<Supplier<Duration>> refreshExpired = new AtomicReference<>(() -> DEFAULT_EXPIRED_RFS);

    private JwtUtil() {
    }

    public static JwtResult encode(MarsAccessToken claims) {
        Assert.notNull(secret.get(), "secret SigningKey is null");
        Assert.isTrue(StringUtils.isNoneBlank(claims.getSub()), "JWT Subject cannot be empty");

        Assert.isTrue(StringUtils.isNoneBlank(claims.getAud()), "JWT Audience cannot be empty");
        Assert.isTrue(AUDIENCES.contains(claims.getAud()), "Invalid JWT audience");

        Assert.isTrue(StringUtils.isNoneBlank(claims.getIss()), "JWT Issuer cannot be empty");
        Assert.isTrue(ISSUERS.contains(claims.getIss()), "Invalid JWT issuer");

        String audience = claims.getAud();
        Claims c = Jwts.claims()
                .setId(UUID.randomUUID().toString())
                .setSubject(claims.getSub())
                .setIssuer(claims.getIss())
                .setAudience(audience)
                .setIssuedAt(claims.getIssuedAt());

        if (audience.equalsIgnoreCase(MarsAccessToken.ACS)) {
            Assert.isTrue(StringUtils.isNoneBlank(claims.getNik()), "NIK claims cannot be empty/null");
            Assert.notNull(claims.getWitel(), "WITEL claims cannot be null");

            c.put("nik", claims.getNik());
            c.put("witel", claims.getWitel());

            if (claims.getExpiredAt() != null)
                c.setExpiration(claims.getExpiredAt());
            else
                c.setExpiration(Date.from(claims
                        .getIssuedAt()
                        .toInstant()
                        .plus(accessExpired.get().get().toMillis(), ChronoUnit.MILLIS)));

            if (claims.getTelegram() != null)
                c.put("tg", claims.getTelegram());

            if (StringUtils.isNoneBlank(claims.getSto()))
                c.put("sto", claims.getSto());

            c.put("roles", claims.getRoles().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet()));
        }
        else if (audience.equalsIgnoreCase(MarsAccessToken.RFS)) {

            if (claims.getExpiredAt() != null)
                c.setExpiration(claims.getExpiredAt());
            else
                c.setExpiration(Date.from(claims
                        .getIssuedAt()
                        .toInstant()
                        .plus(refreshExpired.get().get().toMillis(), ChronoUnit.MILLIS)));
        }

//        return JwtResult.builder()
//                .id(c.getId())
//                .token(Jwts.builder()
//                        .signWith(secret.get())
//                        .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
//                        .setClaims(c)
//                        .compact())
//                .expiredAt(c.getExpiration().toInstant())
//                .build();
        return encode(c);
    }

    public static JwtResult encode(Claims c) {
        return JwtResult.builder()
                .id(c.getId())
                .token(Jwts.builder()
                        .signWith(secret.get())
                        .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                        .setClaims(c)
                        .compact())
                .expiredAt(c.getExpiration().toInstant())
                .build();
    }

    public static JwtParseResult decode(String bearer) {
        String token = "Bearer ".startsWith(bearer) ?
                bearer.substring("Bearer ".length()) :
                bearer;
        JwtParseResult.JwtParseResultBuilder b = JwtParseResult.builder()
                .rawToken(token);
        try {
            Jws<Claims> jws = decodeToken(token);
            Claims claims = jws.getBody();

            if (!ISSUERS.contains(claims.getIssuer()))
                throw new IllegalStateException("unknown issuer");

            MarsAccessToken.MarsAccessTokenBuilder cb = MarsAccessToken.builder()
                    .sub(claims.getSubject())
                    .iss(claims.getIssuer())
                    .aud(claims.getAudience())
                    .expiredAt(claims.getExpiration())
                    .issuedAt(claims.getIssuedAt());

            if (!AUDIENCES.contains(claims.getAudience())) {
                throw new IllegalStateException("invalid audience");
            }
            else {
                if (claims.getAudience().equals(MarsAccessToken.ACS)) {
                    List<String> roles = claims.get("roles", List.class);

                    cb.nik(claims.get("nik", String.class))
                            .telegram(claims.get("tg", Long.class))
                            .witel(Witel.valueOf(claims.get("witel", String.class)))
                            .sto(claims.get("sto", String.class))
                            .roles(roles.stream()
                                    .map(SimpleGrantedAuthority::new)
                                    .collect(Collectors.toList()));

                }

                b.code(JwtParseResult.Code.OK).claims(cb.build());
            }
        }
        catch (ExpiredJwtException ex) {
            b.code(JwtParseResult.Code.ERR_EXPIRED)
                    .message(ex.getMessage());
        }
        catch (UnsupportedJwtException ex) {
            b.code(JwtParseResult.Code.ERR_UNSUPPORTED)
                    .message(ex.getMessage());
        }
        catch (MalformedJwtException ex) {
            b.code(JwtParseResult.Code.ERR_MALFORMED)
                    .message(ex.getMessage());
        }
        catch (SignatureException ex) {
            b.code(JwtParseResult.Code.ERR_SIGNATURE)
                    .message(ex.getMessage());
        }
        catch (Exception ex) {
            b.code(JwtParseResult.Code.ERR)
                    .message(ex.getMessage());
        }

        return b.build();
    }

    public static Jws<Claims> decodeToken(String token) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException {
        return Jwts.parserBuilder()
                .setSigningKey(secret.get())
                .build()
                .parseClaimsJws(token);
    }

    public static void setSecret(String signingKey) {
        secret.set(Keys.hmacShaKeyFor(signingKey.getBytes()));
    }

    public static void setAccessTokenExpiredDuration(Duration duration) {
        accessExpired.set(() -> duration);
    }

    public static void setRefreshTokenExpiredDuration(Duration duration) {
        refreshExpired.set(() -> duration);
    }

    public static void setAccessTokenExpiredDuration(Supplier<Duration> duration) {
        accessExpired.set(duration);
    }

    public static void setRefreshTokenExpiredDuration(Supplier<Duration> duration) {
        refreshExpired.set(duration);
    }


}

package dev.scaraz.mars.security.jwt;

import dev.scaraz.mars.common.domain.response.JwtResult;
import dev.scaraz.mars.common.tools.enums.Witel;
import dev.scaraz.mars.security.authentication.identity.MarsWebToken;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.Assert;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class JwtUtil {
    private static final Duration DEFAULT_EXPIRED_ACS = Duration.ofHours(1);
    private static final Duration DEFAULT_EXPIRED_RFS = Duration.ofHours(12);

    private static final List<String> ISSUERS = List.of(
            MarsWebToken.ISSUER_WEB,
            MarsWebToken.ISSUER_API);
    private static final List<String> AUDIENCES = List.of(
            MarsWebToken.ACS,
            MarsWebToken.RFS);

    private static final AtomicReference<Key> secret = new AtomicReference<>();
    private static final AtomicReference<Supplier<Duration>> accessExpired = new AtomicReference<>(() -> DEFAULT_EXPIRED_ACS);
    private static final AtomicReference<Supplier<Duration>> refreshExpired = new AtomicReference<>(() -> DEFAULT_EXPIRED_RFS);

    private JwtUtil() {
    }

    public static JwtResult encode(Claims c) {
        return JwtResult.builder()
                .id(c.getId())
                .token(Jwts.builder()
                        .signWith(secret.get())
//                        .header(Header.TYPE, Header.JWT_TYPE)
                        .header().type(Header.JWT_TYPE).and()
                        .claims(c)
                        .compact())
                .expiredAt(c.getExpiration().toInstant())
                .build();
    }
    public static JwtResult encode(String tokenId, MarsWebToken claims) {
        Assert.notNull(secret.get(), "secret SigningKey is null");
        Assert.isTrue(StringUtils.isNoneBlank(claims.getSub()), "JWT Subject cannot be empty");

        Assert.isTrue(StringUtils.isNoneBlank(claims.getAud()), "JWT Audience cannot be empty");
        Assert.isTrue(AUDIENCES.contains(claims.getAud()), "Invalid JWT audience");

        Assert.isTrue(StringUtils.isNoneBlank(claims.getIss()), "JWT Issuer cannot be empty");
        Assert.isTrue(ISSUERS.contains(claims.getIss()), "Invalid JWT issuer");

        String audience = claims.getAud();
        ClaimsBuilder c = Jwts.claims()
                .id(Objects.requireNonNullElseGet(tokenId, () -> UUID.randomUUID().toString()))
                .subject(claims.getSub())
                .issuer(claims.getIss())
                .issuedAt(claims.getIssuedAt())
                .audience().add(audience).and();

        if (audience.equalsIgnoreCase(MarsWebToken.ACS)) {
            Assert.isTrue(StringUtils.isNoneBlank(claims.getNik()), "NIK claims cannot be empty/null");
            Assert.notNull(claims.getWitel(), "WITEL claims cannot be null");

            c.add("nik", claims.getNik());
            c.add("witel", claims.getWitel());

            if (claims.getExpiredAt() != null)
                c.expiration(claims.getExpiredAt());
            else
                c.expiration(Date.from(claims
                        .getIssuedAt()
                        .toInstant()
                        .plus(accessExpired.get().get().toMillis(), ChronoUnit.MILLIS)));

            if (claims.getTelegram() != null)
                c.add("tg", claims.getTelegram());

            if (StringUtils.isNoneBlank(claims.getSto()))
                c.add("sto", claims.getSto());

            c.add("roles", claims.getRoles().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet()));
        }
        else if (audience.equalsIgnoreCase(MarsWebToken.RFS)) {

            if (claims.getExpiredAt() != null)
                c.expiration(claims.getExpiredAt());
            else
                c.expiration(Date.from(claims
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
        return encode(c.build());
    }
    public static JwtResult encode(MarsWebToken claims) {
        return encode(null, claims);
    }


    public static JwtParseResult decode(String bearer) {
        String token = "Bearer ".startsWith(bearer) ?
                bearer.substring("Bearer ".length()) :
                bearer;
        JwtParseResult.JwtParseResultBuilder b = JwtParseResult.builder()
                .rawToken(token);
        try {
            Jws<Claims> jws = decodeToken(token);
            Claims claims = jws.getPayload();

            if (!ISSUERS.contains(claims.getIssuer()))
                throw new IllegalStateException("unknown issuer");

            MarsWebToken.MarsWebTokenBuilder cb = MarsWebToken.builder()
                    .sub(claims.getSubject())
                    .iss(claims.getIssuer())
                    .aud(String.join("", claims.getAudience()))
                    .expiredAt(claims.getExpiration())
                    .issuedAt(claims.getIssuedAt());

            if (AUDIENCES.stream().noneMatch(aud -> claims.getAudience().contains(aud))) {
                throw new IllegalStateException("invalid audience");
            }
            else {
                if (claims.getAudience().contains(MarsWebToken.ACS)) {
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
        return Jwts.parser()
                .verifyWith((SecretKey) secret.get())
                .build()
                .parseSignedClaims(token);
    }

    public static void setSecret(String signingKey) {
        secret.set(Keys.hmacShaKeyFor(signingKey.getBytes()));
    }

    public static void setAccessTokenExpiredDuration(Supplier<Duration> duration) {
        accessExpired.set(duration);
    }

    public static void setRefreshTokenExpiredDuration(Supplier<Duration> duration) {
        refreshExpired.set(duration);
    }


}

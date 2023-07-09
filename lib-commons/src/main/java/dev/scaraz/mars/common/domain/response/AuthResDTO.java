package dev.scaraz.mars.common.domain.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AuthResDTO implements Serializable {

    private String code;

    private String accessToken;
    private Long expiredAt;

    private String refreshToken;
    private Long refreshExpiredAt;

    @JsonIgnore
    private Object user;

    @JsonIgnore
    private long issuedAt;

    @JsonIgnore
    private JwtResult accessTokenResult;
    @JsonIgnore
    private JwtResult refreshTokenResult;

    @JsonIgnore
    @Builder.Default
    private Object[] webTokenPayloads = new Object[2];

}

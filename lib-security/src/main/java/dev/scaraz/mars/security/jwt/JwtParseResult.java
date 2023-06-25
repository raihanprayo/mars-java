package dev.scaraz.mars.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class JwtParseResult {
    public enum Code {
        OK,
        ERR,
        ERR_MALFORMED,
        ERR_UNSUPPORTED,
        ERR_EXPIRED,
        ERR_SIGNATURE;

        public boolean isError() {
            return ordinal() >= ERR.ordinal();
        }
    }

    private Code code;
    private String message;

    private String rawToken;
    private MarsAccessToken claims;
}

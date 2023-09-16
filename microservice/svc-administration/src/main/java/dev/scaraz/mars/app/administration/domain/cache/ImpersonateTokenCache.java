package dev.scaraz.mars.app.administration.domain.cache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("user:impersonate:token")
public class ImpersonateTokenCache {

    @Id
    private String id;

    private String accessToken;

    @TimeToLive
    private long accessTokenExpired;

    private String refreshToken;

    private long refreshTokenExpired;


}

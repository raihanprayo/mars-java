package dev.scaraz.mars.core.domain.cache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("usr:forgot")
public class ForgotPassword {

    @Id
    private String uid;

    private String token;
    private String otp;

    @TimeToLive
    private long ttl;

}

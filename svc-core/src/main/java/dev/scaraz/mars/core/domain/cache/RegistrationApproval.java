package dev.scaraz.mars.core.domain.cache;

import dev.scaraz.mars.common.utils.AppConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import javax.persistence.Id;
import java.util.concurrent.TimeUnit;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(AppConstants.Cache.USR_APPROVAL_NS)
public class RegistrationApproval {
    @Id
    private String id;

    @Builder.Default
    @TimeToLive(unit = TimeUnit.HOURS)
    private long ttl = 24;
}

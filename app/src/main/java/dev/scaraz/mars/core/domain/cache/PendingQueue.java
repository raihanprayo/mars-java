package dev.scaraz.mars.core.domain.cache;

import dev.scaraz.mars.common.utils.AppConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import jakarta.persistence.Id;
import java.util.concurrent.TimeUnit;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(AppConstants.Cache.TC_PENDING_QUEUE)
public class PendingQueue {

    /**
     * Ticket ID
     */
    @Id
    private String id;

    @Builder.Default
    @TimeToLive(unit = TimeUnit.MINUTES)
    private long ttl = 60;

}

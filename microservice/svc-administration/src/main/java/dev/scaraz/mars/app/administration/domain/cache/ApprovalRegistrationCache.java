package dev.scaraz.mars.app.administration.domain.cache;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("user:reg:approval")
public class ApprovalRegistrationCache {
    @Id
    private String id;
    private long ttl;
}

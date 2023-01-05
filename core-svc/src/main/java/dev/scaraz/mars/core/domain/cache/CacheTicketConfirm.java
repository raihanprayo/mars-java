package dev.scaraz.mars.core.domain.cache;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import javax.persistence.Id;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("tc:confirm")
public class CacheTicketConfirm {

    @Id
    private int id;

    private String no;

    @TimeToLive
    @Builder.Default
    private long ttl = -1;

}

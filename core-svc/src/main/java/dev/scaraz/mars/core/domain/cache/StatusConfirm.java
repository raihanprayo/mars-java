package dev.scaraz.mars.core.domain.cache;

import dev.scaraz.mars.common.tools.enums.TcStatus;
import dev.scaraz.mars.common.utils.AppConstants;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import static dev.scaraz.mars.common.utils.AppConstants.Cache.TC_CONFIRM_NS;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(TC_CONFIRM_NS)
public class StatusConfirm {

    @Id
    private int id;

    @NotNull
    private String no;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TcStatus status;

    @TimeToLive
    @Builder.Default
    private long ttl = -1;

}

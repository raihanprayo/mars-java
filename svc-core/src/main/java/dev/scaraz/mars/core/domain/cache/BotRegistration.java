package dev.scaraz.mars.core.domain.cache;

import dev.scaraz.mars.common.tools.enums.RegisterState;
import dev.scaraz.mars.common.tools.enums.Witel;
import dev.scaraz.mars.common.utils.AppConstants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import javax.persistence.Id;
import java.util.concurrent.TimeUnit;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(AppConstants.Cache.USR_REGISTRATION_NS)
public class BotRegistration {
    @Id
    private long id;

    private String username;

    private RegisterState state;

    private String name;

    private String nik;

    private String phone;

    private String subregion;

    private Witel witel;

    private String email;

    @Builder.Default
    @TimeToLive(unit = TimeUnit.MINUTES)
    private long ttl = 5;

}

package dev.scaraz.mars.app.administration.domain.cache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("tc:reg:form")
public class FormTicketRegistrationCache implements Serializable {
    public enum State {
        ISSUE,
        NETWORK,
        PARAM,
        FORM
    }

    @Id
    private long id;

    private State state;

    @TimeToLive(unit = TimeUnit.MINUTES)
    private long ttl;

    private String issueCode;

}

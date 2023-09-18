package dev.scaraz.mars.app.administration.domain.cache;

import dev.scaraz.mars.common.tools.enums.RegisterState;
import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("user:reg:form")
public class FormUserRegistrationCache implements Serializable {

    /**
     * user id
     */
    @Id
    private long id;

    private RegisterState state;

    @TimeToLive
    private long ttl;


    private String name;

    private String nik;

    private String phone;

    private Witel witel;

    private String sto;

}

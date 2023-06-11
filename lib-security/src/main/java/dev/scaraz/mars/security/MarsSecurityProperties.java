package dev.scaraz.mars.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@Setter
@ConfigurationProperties(prefix = "mars.security")
public class MarsSecurityProperties {

    private int passwordStrength = 10;

    private JwtProperties jwt;

    @Getter
    @Setter
    public static final class JwtProperties {
        private String secret;
        private String tokenPrefix = "Bearer";
        private Duration tokenDuration = Duration.ofHours(1);
        private Duration refreshTokenDuration = Duration.ofHours(6);
    }
}

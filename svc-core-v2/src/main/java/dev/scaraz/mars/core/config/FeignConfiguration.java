package dev.scaraz.mars.core.config;

import dev.scaraz.mars.common.domain.general.AccessToken;
import feign.Feign;
import feign.Logger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
@Configuration
public class FeignConfiguration {

    @Bean
    public Feign.Builder feignBuilder() {
        return Feign.builder()
                .requestInterceptor(req -> {
                    Authentication auth = SecurityContextHolder.getContext()
                            .getAuthentication();

                    if (auth != null) {
                        AccessToken accessToken = (AccessToken) auth.getPrincipal();
                        req.header("Authorization", "Bearer " + accessToken);
                    }
                });
    }

    @Bean
    public Logger.Level logLevel() {
        return Logger.Level.FULL;
    }

}

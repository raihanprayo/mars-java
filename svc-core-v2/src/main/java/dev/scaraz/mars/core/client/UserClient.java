package dev.scaraz.mars.core.client;

import dev.scaraz.mars.common.domain.general.AccessToken;
import dev.scaraz.mars.core.config.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "mars-user",
        configuration = FeignConfiguration.class,
        fallback = UserClient.UserClientFallback.class)
public interface UserClient {

    @GetMapping("/auth/claims")
    AccessToken authClaims(@RequestHeader("Authorization") String token);

    class UserClientFallback implements UserClient {
        @Override
        public AccessToken authClaims(String token) {
            return null;
        }
    }

}

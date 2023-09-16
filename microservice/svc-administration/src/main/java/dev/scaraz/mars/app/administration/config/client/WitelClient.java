package dev.scaraz.mars.app.administration.config.client;

import dev.scaraz.mars.app.administration.domain.cache.ImpersonateTokenCache;
import dev.scaraz.mars.app.administration.service.TokenExchangeService;
import dev.scaraz.mars.app.administration.service.app.UserService;
import dev.scaraz.mars.common.exception.web.InternalServerException;
import dev.scaraz.mars.common.tools.enums.Witel;
import dev.scaraz.mars.telegram.config.TelegramContextHolder;
import feign.Feign;
import feign.Logger;
import feign.Target;
import lombok.RequiredArgsConstructor;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WitelClient {

    private final UserService userService;
    private final TokenExchangeService tokenExchangeService;

    private final DiscoveryClient discoveryClient;

    public WitelApi get(Witel witel) {
        String serviceId = "mars-" + witel.name().toLowerCase();
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
        if (instances.isEmpty())
            throw InternalServerException.args(String.format("%s service unavailable", serviceId));

        return Feign.builder()
                .contract(new SpringMvcContract())
                .logLevel(Logger.Level.BASIC)
                .requestInterceptor(template -> {
                    if (TelegramContextHolder.hasContext()) {
                        UserRepresentation user = userService.findByTelegramId(TelegramContextHolder.getUserId());
                        ImpersonateTokenCache exchange = tokenExchangeService.exchange(user.getId(), witel);
                        template.header("Authorization", bearer(exchange.getAccessToken()));
                    }
                    else if (SecurityContextHolder.getContext() != null) {
                        SecurityContext context = SecurityContextHolder.getContext();
                        if (context.getAuthentication() instanceof KeycloakAuthenticationToken) {
                            KeycloakAuthenticationToken authentication = (KeycloakAuthenticationToken) context.getAuthentication();
                            KeycloakPrincipal<KeycloakSecurityContext> principal = (KeycloakPrincipal<KeycloakSecurityContext>) authentication.getPrincipal();
                            template.header("Authorization", bearer(principal.getKeycloakSecurityContext().getTokenString()));
                        }
                    }
                })
                .build()
                .newInstance(new Target.HardCodedTarget<>(
                        WitelApi.class,
                        instances.get(0).getUri().toString()
                ));
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}

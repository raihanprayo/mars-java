package dev.scaraz.mars.app.witel.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KeycloakApiConfiguration {

    private final KeycloakSpringBootProperties keycloakProperties;

    @Bean
    public Keycloak keycloak() {
        String secret = keycloakProperties.getCredentials().get("secret").toString();

        log.debug("Keycloak server-url: {}", keycloakProperties.getAuthServerUrl());
        return KeycloakBuilder.builder()
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .serverUrl(keycloakProperties.getAuthServerUrl())
                .realm(keycloakProperties.getRealm())
                .clientId(keycloakProperties.getResource())
                .clientSecret(secret)
                .build();
    }

}

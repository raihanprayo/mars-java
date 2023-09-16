package dev.scaraz.mars.app.administration.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(KeycloakSpringBootProperties.class)
public class KeycloakClientConfiguration {

    private final KeycloakSpringBootProperties keycloakProperties;

    @Bean
    public Keycloak keycloak() {
        String secret = keycloakProperties.getCredentials().get("secret").toString();

        log.debug("Keycloak client-id: {}", keycloakProperties.getResource());
        return KeycloakBuilder.builder()
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .serverUrl(keycloakProperties.getAuthServerUrl())
                .realm(keycloakProperties.getRealm())
                .clientId(keycloakProperties.getResource())
                .clientSecret(secret)
                .build();
    }

    @Bean
    public RealmResource keycloakRealm(Keycloak keycloak) {
        return keycloak.realm(keycloakProperties.getRealm());
    }

}

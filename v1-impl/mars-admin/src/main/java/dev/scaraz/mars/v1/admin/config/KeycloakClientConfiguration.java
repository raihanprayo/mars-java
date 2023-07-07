package dev.scaraz.mars.v1.admin.config;

import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.OAuth2Constants;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.client.Client;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class KeycloakClientConfiguration {

    private final KeycloakSpringBootProperties keycloakSpringBootProperties;

    private Client keycloakClient() {
        return new ResteasyClientBuilderImpl()
                .connectionPoolSize(10)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakSpringBootProperties.getAuthServerUrl())
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .realm(keycloakSpringBootProperties.getRealm())
                .clientId(keycloakSpringBootProperties.getResource())
                .clientSecret((String) keycloakSpringBootProperties.getCredentials().get("secret"))
                .resteasyClient(keycloakClient())
                .build();
    }

//    @Bean
//    public RealmResource realmResource(Keycloak kc) {
//        return kc.realm(keycloakSpringBootProperties.getRealm());
//    }

}

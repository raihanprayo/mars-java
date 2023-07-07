package dev.scaraz.mars.admin.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "mars.keycloak-server")
public class KeycloakServerProperties {
    private String contextPath = "/";

    private final KeycloakAdminProperties admin = new KeycloakAdminProperties();

    @Data
    public static class KeycloakAdminProperties {
        private String username;
        private String password;
    }
}

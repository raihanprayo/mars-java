package dev.scaraz.mars.app.administration.config;

import dev.scaraz.mars.common.config.DataSourceAuditor;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "dev.scaraz.mars.app.administration.repository.db")
@EnableJpaAuditing(
        auditorAwareRef = DataSourceAuditor.BEAN_NAME,
        dateTimeProviderRef = DataSourceAuditor.BEAN_NAME)
public class DataSourceConfiguration {

    @Bean(DataSourceAuditor.BEAN_NAME)
    public DataSourceAuditor dataSourceAuditor() {
        return new DataSourceAuditor()
                .addAuditorResolver(() -> {
                    SecurityContext context = SecurityContextHolder.getContext();
                    if (context.getAuthentication() == null) return null;

                    Authentication authentication = context.getAuthentication();
                    if (authentication.getPrincipal() instanceof KeycloakPrincipal) {
                        KeycloakPrincipal<KeycloakSecurityContext> principal = (KeycloakPrincipal<KeycloakSecurityContext>) authentication.getPrincipal();
                        return principal.getKeycloakSecurityContext().getToken().getPreferredUsername();
                    }

                    return null;
                });
    }

    @Bean
    public JpaTransactionManager transactionManager(final EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory);
        return jpaTransactionManager;
    }

}

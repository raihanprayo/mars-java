package dev.scaraz.mars.core.config;

import dev.scaraz.mars.common.config.DataSourceAuditor;
import dev.scaraz.mars.core.domain.credential.User;
import dev.scaraz.mars.core.util.DelegateUser;
import dev.scaraz.mars.security.authentication.identity.MarsAuthentication;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "dev.scaraz.mars.core.repository.db")
@EnableJpaAuditing(
        modifyOnCreate = false,
        dateTimeProviderRef = DataSourceAuditor.BEAN_NAME,
        auditorAwareRef = DataSourceAuditor.BEAN_NAME)
public class DataSourceConfiguration {

    @Bean(DataSourceAuditor.BEAN_NAME)
    public DataSourceAuditor dataSourceAuditor(List<DataSourceAuditor.AuditorResolver> resolvers) {
        DataSourceAuditor auditor = new DataSourceAuditor();
        resolvers.forEach(auditor::addAuditorResolver);
        return auditor.addAuditorResolver(() -> {
            Authentication authentication = SecurityContextHolder.getContext()
                    .getAuthentication();

            if (authentication != null && authentication.isAuthenticated()) {
                Object principal = authentication.getPrincipal();
                if (principal instanceof DelegateUser)
                    return ((DelegateUser) principal).getNik();
                else if (principal instanceof User)
                    return ((User) principal).getNik();
                else if (principal instanceof MarsAuthentication)
                    return ((MarsAuthentication) principal).getName();
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

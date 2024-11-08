package dev.scaraz.mars.core.config;

import dev.scaraz.mars.common.config.DataSourceAuditor;
import dev.scaraz.mars.core.domain.credential.Account;
import dev.scaraz.mars.core.util.DelegateUser;
import dev.scaraz.mars.security.authentication.identity.MarsAuthentication;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.List;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "dev.scaraz.mars.core.repository.db",
        considerNestedRepositories = true)
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
                else if (principal instanceof Account)
                    return ((Account) principal).getNik();
                else if (principal instanceof MarsAuthentication)
                    return ((MarsAuthentication) principal).getName();
            }
            return null;
        });
    }

    @Bean
    public JpaTransactionManager transactionManager(final EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

}

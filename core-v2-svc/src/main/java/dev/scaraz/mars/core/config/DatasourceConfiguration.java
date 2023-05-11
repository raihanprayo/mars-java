package dev.scaraz.mars.core.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "dev.scaraz.mars.core.datasource.repo")
@EnableJpaAuditing(
        auditorAwareRef = "auditorAware",
        dateTimeProviderRef = "dateTimeProvider"
)
public class DatasourceConfiguration {

    @Bean
    public JpaTransactionManager transactionManager(final EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory);
        return jpaTransactionManager;
    }

    @Bean
    public DateTimeProvider dateTimeProvider() {
        return () -> {
            Instant instant = Instant.now();
            return Optional.of(instant.atZone(ZoneOffset.of("+07")));
        };
    }

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.of("system");
    }

}

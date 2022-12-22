package dev.scaraz.mars.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "dev.scaraz.mars.core.repository",
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = "dev\\.scaraz\\.mars\\.core\\.repository\\.cache\\..*"))
@EnableJpaAuditing(
        modifyOnCreate = false,
        dateTimeProviderRef = "coreAuditorAware",
        auditorAwareRef = "coreAuditorAware")
public class DataSourceConfiguration {

    @Bean
    public JpaTransactionManager transactionManager(final EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory);
        return jpaTransactionManager;
    }

}

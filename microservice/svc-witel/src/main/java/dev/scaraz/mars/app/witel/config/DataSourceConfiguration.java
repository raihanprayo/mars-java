package dev.scaraz.mars.app.witel.config;

import dev.scaraz.mars.common.config.DataSourceAuditor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "dev.scaraz.mars.app.witel.repository")
@EnableJpaAuditing(
        auditorAwareRef = DataSourceAuditor.BEAN_NAME,
        dateTimeProviderRef = DataSourceAuditor.BEAN_NAME)
public class DataSourceConfiguration {

    @Bean(DataSourceAuditor.BEAN_NAME)
    public DataSourceAuditor dataSourceAuditor() {
        return new DataSourceAuditor();
    }

    @Bean
    public JpaTransactionManager transactionManager(final EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory);
        return jpaTransactionManager;
    }

}

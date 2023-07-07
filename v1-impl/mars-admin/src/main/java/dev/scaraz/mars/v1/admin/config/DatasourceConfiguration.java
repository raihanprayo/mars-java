package dev.scaraz.mars.v1.admin.config;

import dev.scaraz.mars.common.config.DataSourceAuditor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;

@Configuration
@EnableJpaAuditing(
        modifyOnCreate = false,
        dateTimeProviderRef = DataSourceAuditor.BEAN_NAME,
        auditorAwareRef = DataSourceAuditor.BEAN_NAME)
@EnableJpaRepositories(
        basePackages = "dev.scaraz.mars.v1.admin.repository.db")
@EnableTransactionManagement
public class DatasourceConfiguration {

    @Bean(DataSourceAuditor.BEAN_NAME)
    public DataSourceAuditor datasourceAuditor() {
        return new DataSourceAuditor();
    }

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(emf);
        return tm;
    }

}

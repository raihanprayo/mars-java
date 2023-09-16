package dev.scaraz.mars.app.administration.config.extern;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        entityManagerFactoryRef = "witelEntityManagerFactory",
        transactionManagerRef = "witelTransactionManager",
        basePackages = "dev.scaraz.mars.app.administration.repository.extern")
public class WitelDataSourceConfiguration {

    @Bean("witelDatasource")
    @ConfigurationProperties("witel.datasource")
    public DataSource witelDatasource() {
        return DataSourceBuilder.create().build();
    }

    @Bean("witelEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean witelEntityManagerFactory(
            EntityManagerFactoryBuilder managerFactoryBuilder,
            @Qualifier("witelDatasource") DataSource dataSource
    ) {
        LocalContainerEntityManagerFactoryBean admin = managerFactoryBuilder.dataSource(dataSource)
                .persistenceUnit("admin")
                .packages("dev.scaraz.mars.app.administration.domain.extern")
                .build();
        return admin;
    }

    @Bean("witelTransactionManager")
    public JpaTransactionManager witelTransactionManager(@Qualifier("witelEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

}

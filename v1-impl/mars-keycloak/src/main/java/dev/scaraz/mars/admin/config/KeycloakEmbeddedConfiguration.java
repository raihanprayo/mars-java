package dev.scaraz.mars.admin.config;

import dev.scaraz.mars.admin.config.filter.KeycloakSessionFilter;
import dev.scaraz.mars.admin.config.properties.KeycloakServerProperties;
import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.keycloak.platform.Platform;
import org.keycloak.platform.PlatformProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.naming.*;
import javax.naming.spi.NamingManager;
import javax.sql.DataSource;
import java.util.concurrent.Executor;

@Configuration
@RequiredArgsConstructor
public class KeycloakEmbeddedConfiguration implements AsyncConfigurer {

    private final KeycloakServerProperties serverProperties;

    @Bean("keycloak-executor")
    public Executor keycloakServerExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("kc-");
        return executor;
    }

    @Bean
    public ServletRegistrationBean<?> keycloakJaxRsApplication(
            ApplicationContext context,
            DataSource dataSource
    ) throws NamingException {
        KeycloakEmbeddedApplication.context.set(context);
        mockJndiEnvironment(dataSource);
        ServletRegistrationBean<HttpServlet30Dispatcher> servlet = new ServletRegistrationBean<>(new HttpServlet30Dispatcher());
        servlet.addInitParameter("javax.ws.rs.Application", KeycloakEmbeddedApplication.class.getName());
        servlet.addInitParameter(ResteasyContextParameters.RESTEASY_SERVLET_MAPPING_PREFIX, serverProperties.getContextPath());
        servlet.addInitParameter(ResteasyContextParameters.RESTEASY_USE_CONTAINER_FORM_PARAMS, "true");
        servlet.addUrlMappings(serverProperties.getContextPath() + "/*");
        servlet.setLoadOnStartup(1);
        servlet.setAsyncSupported(true);
        return servlet;
    }

    @Bean
    public FilterRegistrationBean<KeycloakSessionFilter> keycloakSessionManagement() {
        FilterRegistrationBean<KeycloakSessionFilter> filter = new FilterRegistrationBean<>();
        filter.setName("Keycloak Session Management");
        filter.setFilter(new KeycloakSessionFilter());
        filter.addUrlPatterns(serverProperties.getContextPath() + "/*");
        return filter;
    }

    @Bean
    @ConditionalOnMissingBean(name = "springBootPlatform")
    protected PlatformProvider springBootPlatform() {
        return Platform.getPlatform();
    }

//    @Bean
//    public ApplicationListener<ApplicationReadyEvent> onServerApplicationReady() {
//        return event -> {
//        };
//    }

    private void mockJndiEnvironment(DataSource ds) throws NamingException {
        NamingManager.setInitialContextFactoryBuilder(env -> environment -> new InitialContext() {
            @Override
            public Object lookup(Name name) throws NamingException {
                return lookup(name.toString());
            }

            @Override
            public Object lookup(String name) throws NamingException {
                switch (name) {
                    case "spring/datasource":
                        return ds;
                    case "java:jboss/ee/concurrency/executor/":
                        return keycloakServerExecutor();
                }
                return super.lookup(name);
            }

            @Override
            public NameParser getNameParser(String name) {
                return CompositeName::new;
            }

            @Override
            public void close() {
            }
        });
    }

}

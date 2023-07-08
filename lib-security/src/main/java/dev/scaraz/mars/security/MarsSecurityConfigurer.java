package dev.scaraz.mars.security;

import dev.scaraz.mars.security.authentication.provider.MarsBearerAuthenticationProvider;
import dev.scaraz.mars.security.authentication.MarsBearerFilter;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public abstract class MarsSecurityConfigurer {

    protected void configure(HttpSecurity http) throws Exception {
    }

    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    }

    @Bean
    public MethodInvokingFactoryBean setSecurityContextStrategy() {
        MethodInvokingFactoryBean methodInvokingFactoryBean = new MethodInvokingFactoryBean();
        methodInvokingFactoryBean.setTargetClass(SecurityContextHolder.class);
        methodInvokingFactoryBean.setTargetMethod("setStrategyName");
        methodInvokingFactoryBean.setArguments(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
        return methodInvokingFactoryBean;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationManagerBuilder authBuilder, AuthenticationConfiguration authConfig) throws Exception {
        configure(authBuilder);
        authBuilder.authenticationProvider(marsBearerAuthenticationProvider());
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        configure(http.addFilterBefore(marsBearerFilter(), UsernamePasswordAuthenticationFilter.class));
        return http.build();
    }

    protected MarsBearerAuthenticationProvider marsBearerAuthenticationProvider() {
        return new MarsBearerAuthenticationProvider();
    }

    protected MarsBearerFilter marsBearerFilter() {
        return new MarsBearerFilter();
    }

}

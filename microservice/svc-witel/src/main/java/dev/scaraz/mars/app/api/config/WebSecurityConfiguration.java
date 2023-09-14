package dev.scaraz.mars.app.api.config;

import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

@KeycloakConfiguration
@RequiredArgsConstructor
@Import(KeycloakSpringBootConfigResolver.class)
public class WebSecurityConfiguration extends KeycloakWebSecurityConfigurerAdapter {

    private final SecurityProblemSupport problemSupport;

    @Bean
    public MethodInvokingFactoryBean setSecurityStrategy() {
        MethodInvokingFactoryBean m = new MethodInvokingFactoryBean();
        m.setTargetClass(SecurityContextHolder.class);
        m.setTargetMethod("setStrategy");
        m.setArguments(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
        return m;
    }

    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable).cors();
        http
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(problemSupport)
                        .accessDeniedHandler(problemSupport))
                .authorizeRequests(a -> a
                        .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated());
    }

}


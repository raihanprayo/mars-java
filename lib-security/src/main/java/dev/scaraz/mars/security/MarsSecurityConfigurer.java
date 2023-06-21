package dev.scaraz.mars.security;

import dev.scaraz.mars.security.jwt.JwtRequestFilter;
import dev.scaraz.mars.security.jwt.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

public abstract class MarsSecurityConfigurer implements ApplicationContextAware {

    protected ApplicationContext applicationContext;
    protected MarsSecurityProperties securityProperties;
    protected SecurityProblemSupport problemSupport;

    protected void configure(HttpSecurity security) throws Exception {
    }

    protected void configure(AuthenticationManagerBuilder authBuilder) {
    }

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil(securityProperties);
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationManagerBuilder authBuilder,
            AuthenticationConfiguration authConfiguration) throws Exception {
        authBuilder.authenticationProvider(new MarsAuthenticationProvider());
        configure(authBuilder);
        return authConfiguration.getAuthenticationManager();
    }

    @Bean
    public JwtRequestFilter jwtRequestFilter(AuthenticationManager authManager) {
        return new JwtRequestFilter(authManager, securityProperties);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(JwtRequestFilter jwtRequestFilter, HttpSecurity security) throws Exception {
        security.cors().and()
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(eh -> eh
                        .accessDeniedHandler(problemSupport)
                        .authenticationEntryPoint(problemSupport))
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);


        this.configure(security);
        return security.build();
    }

    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("");
    }

    @Bean
    public MarsDatasourceAuditor marsDatasourceAuditor() {
        return new MarsDatasourceAuditor();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        this.securityProperties = applicationContext.getBean(MarsSecurityProperties.class);
        this.problemSupport = applicationContext.getBean(SecurityProblemSupport.class);
        SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
    }

}

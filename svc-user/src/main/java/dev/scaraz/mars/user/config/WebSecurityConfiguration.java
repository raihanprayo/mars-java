package dev.scaraz.mars.user.config;

import dev.scaraz.mars.common.config.properties.MarsProperties;
import dev.scaraz.mars.common.tools.security.JwtProvider;
import dev.scaraz.mars.user.config.security.JwtRequestFilter;
import dev.scaraz.mars.user.config.security.MarsAuthorityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Import({SecurityProblemSupport.class})
public class WebSecurityConfiguration {

    private final MarsProperties marsProperties;
    private final SecurityProblemSupport problemSupport;
    private final JwtRequestFilter jwtRequestFilter;

    @Bean
    public GrantedAuthoritiesMapper authoritiesMapper() {
        return new MarsAuthorityMapper();
    }

    @Bean
    public MethodInvokingFactoryBean methodInvokingFactoryBean() {
        MethodInvokingFactoryBean methodInvokingFactoryBean = new MethodInvokingFactoryBean();
        methodInvokingFactoryBean.setTargetClass(SecurityContextHolder.class);
        methodInvokingFactoryBean.setTargetMethod("setStrategyName");
        methodInvokingFactoryBean.setArguments(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
        return methodInvokingFactoryBean;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(15);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler(problemSupport)
                        .authenticationEntryPoint(problemSupport))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeRequests(req -> req
                        .antMatchers(HttpMethod.POST).permitAll()
                        .antMatchers("/internal/**").permitAll()
                        .antMatchers("/auth/**").permitAll()
                        .anyRequest().authenticated())

                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception {
        AuthenticationManager authenticationManager = authConfiguration.getAuthenticationManager();
        jwtRequestFilter.setAuthenticationManager(authenticationManager);
        return authenticationManager;
    }

    @Bean
    public JwtProvider jwtProvider() {
        JwtProvider jwtProvider = new JwtProvider(marsProperties);
        jwtRequestFilter.setJwtProvider(jwtProvider);
        return jwtProvider;
    }
}

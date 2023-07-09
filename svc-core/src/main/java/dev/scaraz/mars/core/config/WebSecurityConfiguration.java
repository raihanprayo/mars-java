package dev.scaraz.mars.core.config;

import dev.scaraz.mars.common.config.properties.MarsProperties;
import dev.scaraz.mars.common.utils.ConfigConstants;
import dev.scaraz.mars.core.service.ConfigService;
import dev.scaraz.mars.security.MarsPasswordEncoder;
import dev.scaraz.mars.security.MarsSecurityConfigurer;
import dev.scaraz.mars.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;
import org.springframework.session.FlushMode;
import org.springframework.session.SaveMode;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

@Slf4j
@RequiredArgsConstructor

@Configuration
@EnableWebSecurity
@EnableRedisHttpSession(
        flushMode = FlushMode.IMMEDIATE,
        saveMode = SaveMode.ALWAYS)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends MarsSecurityConfigurer {

    private static final int SALT = 14;

    private final SecurityProblemSupport securityProblemSupport;

    private final MarsProperties marsProperties;
    private final ConfigService configService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        JwtUtil.setSecret(marsProperties.getSecret());
        JwtUtil.setAccessTokenExpiredDuration(() -> configService.get(ConfigConstants.JWT_TOKEN_EXPIRED_DRT).getAsDuration());
        JwtUtil.setRefreshTokenExpiredDuration(() -> configService.get(ConfigConstants.JWT_TOKEN_REFRESH_EXPIRED_DRT).getAsDuration());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(eh -> eh
                        .accessDeniedHandler(securityProblemSupport)
                        .authenticationEntryPoint(securityProblemSupport)
                )
                .sessionManagement(s -> s
                        .sessionAuthenticationStrategy(sessionAuthenticationStrategy())
                        .sessionCreationPolicy(SessionCreationPolicy.NEVER))
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .authorizeRequests(r -> r
                        .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .antMatchers(HttpMethod.GET, "/app/test").permitAll()
                        .antMatchers(
                                "/auth/token",
                                "/auth/refresh",
                                "/auth/forgot/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                );
    }

    @Bean
    public MarsPasswordEncoder passwordEncoder() {
        MarsPasswordEncoder.setPasswordStrength(SALT);
        return new MarsPasswordEncoder();
    }

    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("");
    }

    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        SessionFixationProtectionStrategy sessionStrategy = new SessionFixationProtectionStrategy();
        sessionStrategy.setMigrateSessionAttributes(true);
        sessionStrategy.setAlwaysCreateSession(false);
        return sessionStrategy;
    }

}


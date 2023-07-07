package dev.scaraz.mars.core.v2.config;

import dev.scaraz.mars.common.config.properties.MarsProperties;
import dev.scaraz.mars.core.v2.service.app.ConfigService;
import dev.scaraz.mars.core.v2.util.ConfigConstants;
import dev.scaraz.mars.security.MarsJwtConfigurer;
import dev.scaraz.mars.security.MarsPasswordEncoder;
import dev.scaraz.mars.security.MarsSecurityConfigurer;
import dev.scaraz.mars.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Import({SecurityProblemSupport.class})
public class WebSecurityConfiguration extends MarsSecurityConfigurer {

    private final SecurityProblemSupport problemSupport;

    private final MarsProperties marsProperties;

    private final ConfigService configService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        JwtUtil.setSecret(marsProperties.getSecret());
        JwtUtil.setAccessTokenExpiredDuration(() -> configService
                .get(ConfigConstants.JWT_TOKEN_EXPIRED_DRT)
                .getAs(Duration::parse));
        JwtUtil.setRefreshTokenExpiredDuration(() -> configService
                .get(ConfigConstants.JWT_TOKEN_REFRESH_EXPIRED_DRT)
                .getAs(Duration::parse));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors().and()
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .exceptionHandling(eh -> eh
                        .authenticationEntryPoint(problemSupport)
                        .accessDeniedHandler(problemSupport)
                )
                .sessionManagement(s -> s
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeRequests(r -> r
                        .antMatchers(
                                "/auth/token",
                                "/auth/register"
                        ).permitAll()
                        .anyRequest().authenticated()
                );
    }

    @Bean
    public MarsPasswordEncoder passwordEncoder() {
        return new MarsPasswordEncoder();
    }

}

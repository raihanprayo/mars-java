package dev.scaraz.mars.core.config;

import dev.scaraz.mars.common.config.properties.MarsProperties;
import dev.scaraz.mars.common.utils.ConfigConstants;
import dev.scaraz.mars.core.service.ConfigService;
import dev.scaraz.mars.security.MarsPasswordEncoder;
import dev.scaraz.mars.security.MarsSecurityConfigurer;
import dev.scaraz.mars.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor

@Configuration
@Import({SecurityProblemSupport.class})
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends MarsSecurityConfigurer {

    private static final int SALT = 14;

    private final SecurityProblemSupport problemSupport;

//    private final JwtRequestFilter jwtRequestFilter;

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
                        .accessDeniedHandler(problemSupport)
                        .authenticationEntryPoint(problemSupport)
                )
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
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

}


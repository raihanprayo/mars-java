package dev.scaraz.mars.v1.core.config;

import dev.scaraz.mars.common.config.properties.MarsProperties;
import dev.scaraz.mars.security.MarsSecurityConfigurer;
import dev.scaraz.mars.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        JwtUtil.setSecret(marsProperties.getSecret());
        JwtUtil.setAccessTokenExpiredDuration(Duration.ofHours(2));
        JwtUtil.setRefreshTokenExpiredDuration(Duration.ofHours(24));
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
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(SALT);
    }


//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
//        return authenticationConfiguration
//                .getAuthenticationManager();
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        log.debug("CORS {}", marsProperties.getCors().getAllowedOrigins());
//        return http
//                .cors().and()
//                .csrf(AbstractHttpConfigurer::disable)
//                .exceptionHandling(eh -> eh
//                        .accessDeniedHandler(problemSupport)
//                        .authenticationEntryPoint(problemSupport)
//                )
//                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .formLogin(AbstractHttpConfigurer::disable)
//                .logout(AbstractHttpConfigurer::disable)
//                .authorizeRequests(r -> r
//                        .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
//                        .antMatchers(HttpMethod.GET, "/app/test").permitAll()
//                        .antMatchers(
//                                "/auth/token",
//                                "/auth/refresh"
//                        ).permitAll()
//                        .anyRequest().authenticated()
//                )
//                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
//                .build();
//    }

    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("");
    }

}


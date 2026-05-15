package com.example.sessionsecurity.security;

import com.example.sessionsecurity.common.datasource.DbUserRoutingFilter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(AppSecurityProperties.class)
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            ObjectProvider<DbUserRoutingFilter> dbUserRoutingFilterProvider,
            SessionAuthenticationFilter sessionAuthenticationFilter,
            JsonAuthenticationEntryPoint authenticationEntryPoint,
            JsonAccessDeniedHandler accessDeniedHandler
    ) throws Exception {
        HttpSecurity configuredHttp = http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/actuator/health"
                        ).permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/manager/**").hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers("/api/user/**").hasAnyRole("USER", "MANAGER", "ADMIN")
                        .requestMatchers("/api/common/**").authenticated()
                        .anyRequest().authenticated());

        DbUserRoutingFilter dbUserRoutingFilter = dbUserRoutingFilterProvider.getIfAvailable();
        if (dbUserRoutingFilter != null) {
            configuredHttp.addFilterBefore(dbUserRoutingFilter, SessionAuthenticationFilter.class);
        }

        configuredHttp.addFilterBefore(sessionAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return configuredHttp.build();
    }
}

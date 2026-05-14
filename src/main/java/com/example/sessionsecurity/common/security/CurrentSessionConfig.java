package com.example.sessionsecurity.common.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CurrentSessionConfig {

    @Bean
    CurrentSessionProvider currentSessionProvider() {
        return CurrentSessionProvider.fromSecurityContext();
    }
}

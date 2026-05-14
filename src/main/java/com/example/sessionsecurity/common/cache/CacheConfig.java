package com.example.sessionsecurity.common.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@Configuration
@EnableConfigurationProperties(AppCacheProperties.class)
public class CacheConfig {

    @Bean
    @ConditionalOnProperty(prefix = "app.cache", name = "type", havingValue = "simple", matchIfMissing = true)
    CacheManager simpleCacheManager(AppCacheProperties properties) {
        return new ConcurrentMapCacheManager(properties.getCacheNames().toArray(String[]::new));
    }

    @Bean
    @ConditionalOnProperty(prefix = "app.cache", name = "type", havingValue = "caffeine")
    CacheManager caffeineCacheManager(AppCacheProperties properties) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
                properties.getCacheNames().toArray(String[]::new)
        );
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(1_000)
                .expireAfterWrite(Duration.ofMinutes(10)));
        return cacheManager;
    }
}

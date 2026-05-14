package com.example.sessionsecurity.sample.cache;

import java.time.LocalDateTime;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CacheSampleService {

    @Cacheable(cacheNames = "sampleSimple", key = "#id")
    public String getSimpleCachedValue(String id) {
        return "simple cache value for " + id + " at " + LocalDateTime.now();
    }

    @Cacheable(cacheNames = "sampleCaffeine", key = "#id")
    public String getCaffeineCachedValue(String id) {
        return "caffeine cache value for " + id + " at " + LocalDateTime.now();
    }

    @CacheEvict(cacheNames = {"sampleSimple", "sampleCaffeine"}, allEntries = true)
    public void clear() {
    }
}

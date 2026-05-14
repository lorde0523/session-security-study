package com.example.sessionsecurity.common.cache;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.cache")
public class AppCacheProperties {

    private String type = "simple";
    private List<String> cacheNames = new ArrayList<>(List.of("sampleSimple", "sampleCaffeine"));

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getCacheNames() {
        return cacheNames;
    }

    public void setCacheNames(List<String> cacheNames) {
        this.cacheNames = cacheNames;
    }
}

package com.example.sessionsecurity.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
public class AppSecurityProperties {

    private boolean headerSessionEnabled;

    public boolean isHeaderSessionEnabled() {
        return headerSessionEnabled;
    }

    public void setHeaderSessionEnabled(boolean headerSessionEnabled) {
        this.headerSessionEnabled = headerSessionEnabled;
    }
}

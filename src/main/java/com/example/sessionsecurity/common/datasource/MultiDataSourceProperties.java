package com.example.sessionsecurity.common.datasource;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.datasource")
public class MultiDataSourceProperties {

    private boolean routingEnabled;
    private String defaultUser = "main";
    private Map<String, DbUserProperties> users = new LinkedHashMap<>();
    private Map<String, String> urlUserMappings = new LinkedHashMap<>();

    public boolean isRoutingEnabled() {
        return routingEnabled;
    }

    public void setRoutingEnabled(boolean routingEnabled) {
        this.routingEnabled = routingEnabled;
    }

    public Map<String, DbUserProperties> getUsers() {
        return users;
    }

    public void setUsers(Map<String, DbUserProperties> users) {
        this.users = users;
    }

    public String getDefaultUser() {
        return defaultUser;
    }

    public void setDefaultUser(String defaultUser) {
        this.defaultUser = defaultUser;
    }

    public Map<String, String> getUrlUserMappings() {
        return urlUserMappings;
    }

    public void setUrlUserMappings(Map<String, String> urlUserMappings) {
        this.urlUserMappings = urlUserMappings;
    }

    public static class DbUserProperties {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}

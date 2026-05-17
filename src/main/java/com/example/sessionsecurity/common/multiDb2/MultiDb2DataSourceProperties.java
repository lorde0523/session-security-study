package com.example.sessionsecurity.common.multiDb2;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.datasource")
public class MultiDb2DataSourceProperties {

    private boolean enumRoutingEnabled;
    private String driverClassName;
    private String url;
    private String defaultKey;
    private Map<String, User> users = new LinkedHashMap<>();

    public boolean isEnumRoutingEnabled() {
        return enumRoutingEnabled;
    }

    public void setEnumRoutingEnabled(boolean enumRoutingEnabled) {
        this.enumRoutingEnabled = enumRoutingEnabled;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDefaultKey() {
        return defaultKey;
    }

    public void setDefaultKey(String defaultKey) {
        this.defaultKey = defaultKey;
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public void setUsers(Map<String, User> users) {
        this.users = users;
    }

    public static class User {

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

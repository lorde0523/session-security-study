package com.example.sessionsecurity.common.datasource;

public class DbUserContext {

    private static final ThreadLocal<String> CURRENT = new ThreadLocal<>();

    private DbUserContext() {
    }

    public static void set(String userKey) {
        CURRENT.set(userKey);
    }

    public static String get() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.remove();
    }
}

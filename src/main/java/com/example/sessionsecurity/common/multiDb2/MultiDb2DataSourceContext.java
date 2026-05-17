package com.example.sessionsecurity.common.multiDb2;

public class MultiDb2DataSourceContext {

    private static final ThreadLocal<DataSourceKey> CURRENT = new ThreadLocal<>();

    private MultiDb2DataSourceContext() {
    }

    public static void set(DataSourceKey key) {
        CURRENT.set(key);
    }

    public static DataSourceKey get() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.remove();
    }
}

package com.unity.core_service.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicRoutingDataSource extends AbstractRoutingDataSource {

    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    public static void setCurrentService(String serviceName) {
        contextHolder.set(serviceName);
    }

    public static String getCurrentService() {
        return contextHolder.get();
    }

    public static void clear() {
        contextHolder.remove();
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return getCurrentService();
    }
}
package com.unity.core_service.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.unity.core_service.repository.ServiceConfigurationRepository;

import jakarta.annotation.PostConstruct;

@Configuration
public class DynamicDataSourceConfig {

    @Bean
//    @DependsOn("entityManagerFactory")
    public DataSource dataSource(ServiceConfigurationRepository repository) {
        Map<Object, Object> dataSources = new HashMap<>();

        repository.findAll().forEach(config -> {
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setUrl(config.getDbUrl());
            dataSource.setUsername(config.getDbUsername());
            dataSource.setPassword(config.getDbPassword());
            dataSources.put(config.getServiceName(), dataSource);
        });

        DynamicRoutingDataSource routingDataSource = new DynamicRoutingDataSource();
        routingDataSource.setTargetDataSources(dataSources);
        return routingDataSource;
    }
    
    @PostConstruct
    public void init() {
        System.out.println("DynamicDataSourceConfig initialized");
    }
}
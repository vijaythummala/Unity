package com.unity.payment_service.config;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StartupConfig {

@Bean
ThreadPoolExecutor threadPoolExecutor() {
    return new ThreadPoolExecutor(
        0, // core pool size
        5, // maximum pool size
        1000, // keep-alive time
        TimeUnit.MILLISECONDS, // time unit for keep-alive
        new ArrayBlockingQueue<>(10) // work queue with a capacity of 10
    );
}
}

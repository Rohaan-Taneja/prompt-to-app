package com.PromptToApp.core.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class asyncThreadPoolConfig {


    @Bean("background-task-executor-thread-pool")
    public Executor executorPool(){
        ThreadPoolTaskExecutor threadPool = new ThreadPoolTaskExecutor();

        threadPool.setCorePoolSize(10);
        threadPool.setMaxPoolSize(30);
        threadPool.setQueueCapacity(150);
        threadPool.setThreadNamePrefix("back-thread-");

        threadPool.initialize();

        return threadPool;

    }
}

package com.ampmangu.degrees.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;


@Configuration
@EnableScheduling
public class AsyncConfiguration implements SchedulingConfigurer {
    private static final int POOL_SIZE = 10;
    private final Logger log = LoggerFactory.getLogger(AsyncConfiguration.class);

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        log.info("Configuring tasks");
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();

        threadPoolTaskScheduler.setPoolSize(POOL_SIZE);
        threadPoolTaskScheduler.setThreadNamePrefix("7degrees-task-pool-");
        threadPoolTaskScheduler.initialize();

        taskRegistrar.setTaskScheduler(threadPoolTaskScheduler);
    }
}

package com.konopleva.crudeapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;
import java.util.concurrent.Executor;

@Configuration
public class AsyncExecutorConfig implements AsyncConfigurer {
    private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

    @Override
    public Executor getAsyncExecutor() {
        var executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(AVAILABLE_PROCESSORS + 1);
        executor.setMaxPoolSize(AVAILABLE_PROCESSORS + 1);
        executor.setThreadNamePrefix("async-exec-");
        executor.initialize();
        return new DelegatingSecurityContextAsyncTaskExecutor(executor);

    }
}

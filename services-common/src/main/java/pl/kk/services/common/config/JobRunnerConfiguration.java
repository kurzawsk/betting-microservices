package pl.kk.services.common.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableFeignClients(basePackages = {"pl.kk.services.common.service.job"})
@ComponentScan(basePackages = {"pl.kk.services.common.service.job"})
public class JobRunnerConfiguration {

    @Bean("threadPoolTaskExecutor")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("AsyncJobThread-");
        executor.initialize();
        return new DelegatingSecurityContextTaskExecutor(executor);
    }
}

package gov.tna.discovery.taxonomy;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfiguration {

    public @Bean Executor asyncExecutor() {
	ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	executor.setCorePoolSize(7);
	executor.setMaxPoolSize(42);
	executor.setQueueCapacity(11);
	executor.setThreadNamePrefix("MyExecutor-");
	executor.initialize();
	return executor;
    }
}
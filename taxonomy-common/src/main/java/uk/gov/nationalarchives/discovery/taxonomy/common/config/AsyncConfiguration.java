package uk.gov.nationalarchives.discovery.taxonomy.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfiguration {

    @Value("${async.fsSearch.threadPoolSize}")
    private Integer fsThreadPoolSize;

    @Value("${async.fsSearch.queueCapacity}")
    private Integer fsQueueCapacity;

    @Value("${async.memorySearch.threadPoolSize}")
    private Integer memoryThreadPoolSize;

    @Value("${async.memorySearch.queueCapacity}")
    private Integer memoryQueueCapacity;

    /**
     * Executor dedicated to searches against lucene index
     * 
     * @return
     */
    public @Bean ThreadPoolTaskExecutor fsSearchTaskExecutor() {
	ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	executor.setCorePoolSize(fsThreadPoolSize);
	executor.setMaxPoolSize(fsThreadPoolSize);
	executor.setQueueCapacity(fsQueueCapacity);
	executor.setThreadNamePrefix("MyExecutor-");
	executor.initialize();
	return executor;
    }

    /**
     * executor dedicated to searches against in memory index
     * 
     * @return
     */
    public @Bean ThreadPoolTaskExecutor memorySearchTaskExecutor() {
	ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	executor.setCorePoolSize(memoryThreadPoolSize);
	executor.setMaxPoolSize(memoryThreadPoolSize);
	executor.setQueueCapacity(memoryQueueCapacity);
	executor.setThreadNamePrefix("MyExecutor-");
	executor.initialize();
	return executor;
    }

}
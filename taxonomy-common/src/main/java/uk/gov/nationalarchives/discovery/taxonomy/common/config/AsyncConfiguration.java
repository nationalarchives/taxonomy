/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
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
	executor.setThreadNamePrefix("fsSearchTaskExecutor-");
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
	executor.setThreadNamePrefix("memorySearchTaskExecutor-");
	executor.initialize();
	return executor;
    }

}
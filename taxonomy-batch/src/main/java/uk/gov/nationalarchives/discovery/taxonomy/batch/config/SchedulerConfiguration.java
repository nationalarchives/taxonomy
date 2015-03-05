package uk.gov.nationalarchives.discovery.taxonomy.batch.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Configuration dedicated to the scheduler: to run daemons
 */
@Configuration
@EnableScheduling
@ConditionalOnProperty(prefix = "batch.role.", value = "udpate-solr-cloud")
class SchedulerConfiguration {

}

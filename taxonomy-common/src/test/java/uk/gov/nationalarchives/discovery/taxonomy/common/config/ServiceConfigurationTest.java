package uk.gov.nationalarchives.discovery.taxonomy.common.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import uk.gov.nationalarchives.discovery.taxonomy.common.repository.legacy.impl.LegacySystemRepositoryImpl;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.MongoConfigurationTest;

@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackages = { "uk.gov.nationalarchives.discovery.taxonomy.common.service",
	"uk.gov.nationalarchives.discovery.taxonomy.common.repository" })
@Import(value = { LuceneConfigurationTest.class, MongoConfigurationTest.class, AsyncConfiguration.class })
public class ServiceConfigurationTest {

    /**
     * This bean is used in EvaluationService but it raises requests to distant
     * service that we don't want to mess in unit test cases
     * 
     * @return
     */
    @Bean
    LegacySystemRepositoryImpl legacySystemRepository() {
	return null;
    };
}
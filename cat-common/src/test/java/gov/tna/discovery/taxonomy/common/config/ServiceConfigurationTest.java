package gov.tna.discovery.taxonomy.common.config;

import gov.tna.discovery.taxonomy.common.repository.mongo.MongoConfigurationTest;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackages = { "gov.tna.discovery.taxonomy.common.service", "gov.tna.discovery.taxonomy.common.repository" })
@Import(value = { LuceneConfigurationTest.class, MongoConfigurationTest.class })
public class ServiceConfigurationTest {
}
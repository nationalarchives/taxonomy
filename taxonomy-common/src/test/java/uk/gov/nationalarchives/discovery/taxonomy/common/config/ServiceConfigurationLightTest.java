package uk.gov.nationalarchives.discovery.taxonomy.common.config;

import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.MongoConfigurationTest;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackages = { "uk.gov.nationalarchives.discovery.taxonomy.common.repository" })
@Import(value = { LuceneConfigurationTest.class, MongoConfigurationTest.class })
public class ServiceConfigurationLightTest {
}
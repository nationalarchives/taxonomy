package uk.gov.nationalarchives.discovery.taxonomy.ws.controller;

import uk.gov.nationalarchives.discovery.taxonomy.common.config.AsyncConfiguration;
import uk.gov.nationalarchives.discovery.taxonomy.common.config.ServiceConfigurationTest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@ComponentScan(basePackages = "uk.gov.nationalarchives.discovery.taxonomy.ws")
@EnableAutoConfiguration
@PropertySource("application.yml")
@Import(value = { ServiceConfigurationTest.class, AsyncConfiguration.class })
public class WSApplicationTest {

    public static void main(String[] args) throws Exception {
	SpringApplication.run(WSApplicationTest.class, args);
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
	return new PropertySourcesPlaceholderConfigurer();
    }

}

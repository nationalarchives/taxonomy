package uk.gov.nationalarchives.discovery.taxonomy.common.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Configuration dedicated to Lucene:<br/>
 * provides all necessary beans (directory, reader, etc)
 * 
 * @author jcharlet
 *
 */
@Configuration
@EnableConfigurationProperties
@ComponentScan(basePackages = "uk.gov.nationalarchives.discovery.taxonomy.common.repository.solr")
@Import({ PropertiesConfiguration.class, SolrConfiguration.class })
public class SolrConfigurationTest {

}
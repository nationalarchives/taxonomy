package uk.gov.nationalarchives.discovery.taxonomy.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "lucene.index")
public class LuceneConfigurationProperties {

}
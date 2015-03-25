package uk.gov.nationalarchives.discovery.taxonomy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import uk.gov.nationalarchives.discovery.taxonomy.common.repository.solr.SolrTaxonomyIAViewRepository;

@ComponentScan
@EnableAutoConfiguration
@PropertySource("application.yml")
public class WSApplication {

    public static void main(String[] args) throws Exception {
	SpringApplication.run(WSApplication.class, args);
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
	return new PropertySourcesPlaceholderConfigurer();
    }

    /**
     * This bean is used in categoriserService but dedicated method is not
     * useful for the WS
     * 
     * @return
     */
    @Bean
    public SolrTaxonomyIAViewRepository solrTaxonomyIAViewRepository() {
	return null;
    }

}

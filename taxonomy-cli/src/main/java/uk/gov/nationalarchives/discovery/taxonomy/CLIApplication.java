package uk.gov.nationalarchives.discovery.taxonomy;

import java.io.IOException;
import java.text.ParseException;

import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

import uk.gov.nationalarchives.discovery.taxonomy.common.repository.solr.SolrTaxonomyIAViewRepository;

@ComponentScan
@EnableAutoConfiguration
@PropertySource("application.yml")
public class CLIApplication {

    public static void main(String[] args) throws IOException, ParseException {
	ConfigurableApplicationContext application = SpringApplication.run(CLIApplication.class, args);
	SpringApplication.exit(application, new ExitCodeGenerator[0]);
    }

    /**
     * This bean is used in categoriserService but dedicated method is not
     * useful for the CLI
     * 
     * @return
     */
    @Bean
    public SolrTaxonomyIAViewRepository solrTaxonomyIAViewRepository() {
	return null;
    }

}

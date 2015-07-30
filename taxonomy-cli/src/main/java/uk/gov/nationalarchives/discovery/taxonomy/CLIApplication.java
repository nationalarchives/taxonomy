/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
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
@PropertySource(value = { "application.yml" })
public class CLIApplication {

    public static void main(String[] args) throws IOException, ParseException {
	ConfigurableApplicationContext application = SpringApplication.run(CLIApplication.class, args);
	SpringApplication.exit(application, new ExitCodeGenerator[0]);
    }

    // FIXME a better approach would be to init categoriserService here with
    // @Bean annotation and depending on the presence of SolrRepo, use a
    // catService constructor that uses it or without
    // see
    // http://stackoverflow.com/questions/19225115/how-to-do-conditional-auto-wiring-in-spring
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

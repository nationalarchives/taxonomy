/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import uk.gov.nationalarchives.discovery.taxonomy.common.repository.legacy.impl.LegacySystemRepositoryImpl;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.solr.SolrTaxonomyIAViewRepository;

@SpringBootApplication
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

    /**
     * This bean is used in EvaluationService but dedicated method is not useful
     * for the WS
     * 
     * @return
     */
    @Bean
    LegacySystemRepositoryImpl legacySystemRepository() {
	return null;
    };

}

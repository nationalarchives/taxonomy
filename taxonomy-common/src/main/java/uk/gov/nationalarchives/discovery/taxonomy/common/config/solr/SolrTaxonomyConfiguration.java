/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.config.solr;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration dedicated to taxonomy related SolR server
 * 
 * @author jcharlet
 *
 */
@Configuration
@ConfigurationProperties(prefix = "solr.taxonomy")
@EnableConfigurationProperties
@ConditionalOnProperty(prefix = "solr.taxonomy", value = "host")
public class SolrTaxonomyConfiguration {
    private  final Logger logger = LoggerFactory.getLogger(this.getClass());

    private String host;

    public String getHost() {
	return host;
    }

    public void setHost(String host) {
	this.host = host;
    }

    /**
     * bean to make query and update requests to solr server
     * 
     * @return the solrServer bean
     */
    public @Bean SolrServer solrTaxonomyServer() {
        logger.info("Solr Taxonomy: {}", host);
	SolrServer server = new HttpSolrServer(host);
	return server;
    }

}
/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.config.solr;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration dedicated to Solr Cloud (cluster of servers used by Discovery)
 * 
 * @author jcharlet
 *
 */
@Configuration
@ConfigurationProperties(prefix = "solr.cloud")
@EnableConfigurationProperties
@ConditionalOnProperty(prefix = "solr.cloud", value = "host")
public class SolrCloudConfiguration {
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
    public
    @Bean
    SolrClient solrCloudServer() {
        logger.info("Solr Cloud: {}", host);

        SolrClient server = new HttpSolrClient(host);
        return server;
    }

}
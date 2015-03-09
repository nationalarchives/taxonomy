package uk.gov.nationalarchives.discovery.taxonomy.common.config;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
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
	SolrServer server = new HttpSolrServer(host);
	return server;
    }

}
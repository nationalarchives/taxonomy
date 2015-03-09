package uk.gov.nationalarchives.discovery.taxonomy.common.config;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
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
@Import({ PropertiesConfiguration.class, SolrCloudConfiguration.class })
public class SolrCloudConfigurationTest {

    // private static final Logger logger =
    // LoggerFactory.getLogger(SolrConfigurationTest.class);

    /**
     * bean to make query and update requests to solr server
     * 
     * @return the solrServer bean
     * @throws IOException
     * @throws SolrServerException
     */
    public @Bean SolrServer solrCloudServer() throws SolrServerException, IOException {
	FileUtils
		.deleteQuietly(new File(
			"/home/jcharlet/_workspace/cat/taxonomy-common/src/test/resources/dataset/solr/iaview_unittest/data/index/write.lock"));

	CoreContainer coreContainer = new CoreContainer(
		"/home/jcharlet/_workspace/cat/taxonomy-common/src/test/resources/dataset/solr");
	coreContainer.load();
	EmbeddedSolrServer embeddedSolrServer = new EmbeddedSolrServer(coreContainer, "iaview_unittest");
	// embeddedSolrServer.optimize();
	return embeddedSolrServer;
    }
}
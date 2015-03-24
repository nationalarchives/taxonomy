package uk.gov.nationalarchives.discovery.taxonomy.common.repository.solr;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrDocument;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.gov.nationalarchives.discovery.taxonomy.common.config.SolrCloudConfigurationTest;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetViewFields;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SolrCloudConfigurationTest.class)
public class SolrIAViewRepositoryTest {

    private static final String DOCREFERENCE = "C508096";
    @Autowired
    private SolrCloudIAViewRepository solrIAViewRepository;

    @Autowired
    SolrServer solrServer;

    @After
    public void setUp() throws Exception {
	solrServer.shutdown();
    }

    @Test
    public final void testGetByDocReference() {
	SolrDocument solrDocument = solrIAViewRepository.getByDocReference(DOCREFERENCE);
	assertThat(solrDocument, is(notNullValue()));
	assertThat(solrDocument.getFieldValue(InformationAssetViewFields.DOCREFERENCE.toString()).toString(),
		equalTo(DOCREFERENCE));
    }

}

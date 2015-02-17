package uk.gov.nationalarchives.discovery.taxonomy.common.repository.solr;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.gov.nationalarchives.discovery.taxonomy.common.config.SolrConfigurationTest;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SolrConfigurationTest.class)
public class SolrIAViewRepositoryTest {

    @Autowired
    private SolrIAViewRepository solrIAViewRepository;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public final void testGetByDocReference() {
	solrIAViewRepository.getByDocReference("C9453483");
	fail("Not yet implemented"); // TODO
    }

    @Test
    public final void testSaveSolrInputDocument() {
	fail("Not yet implemented"); // TODO
    }

    @Test
    public final void testSaveListOfSolrInputDocument() {
	fail("Not yet implemented"); // TODO
    }

}

package uk.gov.nationalarchives.discovery.taxonomy.common.repository.solr;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.gov.nationalarchives.discovery.taxonomy.common.config.SolrConfigurationTest;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetViewFields;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SolrConfigurationTest.class)
@Ignore
public class SolrIAViewRepositoryTest {

    private static final String DOCREFERENCE = "C508096";
    @Autowired
    private SolrIAViewRepository solrIAViewRepository;

    @Autowired
    SolrServer solrServer;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public final void testGetByDocReference() {
	SolrDocument solrDocument = solrIAViewRepository.getByDocReference(DOCREFERENCE);
	assertThat(solrDocument, is(notNullValue()));
	assertThat(solrDocument.getFieldValue(InformationAssetViewFields.DOCREFERENCE.toString()).toString(),
		equalTo(DOCREFERENCE));
    }

    // FIXME 2 fix test case on solr repository: commit generates errors
    @Test
    public final void testSaveSolrInputDocument() {
	String[] categories = generateRandomCategoryArray();

	SolrInputDocument solrInputDocument = createSolrInputDocumentFromDocReferenceAndCategories(DOCREFERENCE,
		categories);
	solrIAViewRepository.save(solrInputDocument);

	SolrDocument solrDocument = solrIAViewRepository.getByDocReference(DOCREFERENCE);
	assertThatUpdatedDocumentHasLatestRandomCategories(categories, solrDocument);

    }

    private void assertThatUpdatedDocumentHasLatestRandomCategories(String[] categories, SolrDocument solrDocument) {
	assertThat(solrDocument, is(notNullValue()));
	assertThat(solrDocument.getFieldValue(InformationAssetViewFields.DOCREFERENCE.toString()).toString(),
		equalTo(DOCREFERENCE));
	assertThat(solrDocument.getFieldValues(InformationAssetViewFields.TAXONOMY.toString()), is(notNullValue()));
	assertThat(solrDocument.getFieldValues(InformationAssetViewFields.TAXONOMY.toString()).toArray(new String[0]),
		equalTo(categories));
    }

    private SolrInputDocument createSolrInputDocumentFromDocReferenceAndCategories(String docReference,
	    String[] categories) {
	SolrInputDocument solrInputDocument = new SolrInputDocument();
	solrInputDocument.addField(InformationAssetViewFields.DOCREFERENCE.toString(), DOCREFERENCE);
	for (String category : categories) {
	    Map<String, Object> addFieldModifier = new HashMap<>(1);
	    addFieldModifier.put("add", category);
	    solrInputDocument.addField(InformationAssetViewFields.TAXONOMY.toString(), addFieldModifier);
	}
	Map<String, Object> removeFieldModifier = new HashMap<>(1);
	removeFieldModifier.put("set", null);
	solrInputDocument.addField(InformationAssetViewFields.TAXONOMY.toString(), removeFieldModifier);
	return solrInputDocument;
    }

    /**
     * I create an array of category with a random element for that reason:
     * Since i cannot have a solr server running in ram (using
     * ramdirectoryFactory, i get an empty core that I have to fill), I must
     * reuse the same document in all my unitTests, with the modifications from
     * previous time
     * 
     * @return
     */
    private String[] generateRandomCategoryArray() {
	Random randomGenerator = new Random();
	String generatedValue = String.valueOf(randomGenerator.nextInt(100));
	String[] categories = new String[] { generatedValue, "Art", "War" };
	return categories;
    }

}

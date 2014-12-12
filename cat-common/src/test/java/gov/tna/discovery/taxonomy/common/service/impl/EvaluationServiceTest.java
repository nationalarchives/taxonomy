package gov.tna.discovery.taxonomy.common.service.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import gov.tna.discovery.taxonomy.common.config.ServiceConfigurationTest;
import gov.tna.discovery.taxonomy.common.repository.domain.TrainingDocument;
import gov.tna.discovery.taxonomy.common.repository.domain.lucene.InformationAssetView;
import gov.tna.discovery.taxonomy.common.repository.domain.mongo.TestDocument;
import gov.tna.discovery.taxonomy.common.repository.lucene.LuceneTestDataSet;
import gov.tna.discovery.taxonomy.common.repository.mongo.MongoTestDataSet;
import gov.tna.discovery.taxonomy.common.repository.mongo.TestDocumentRepository;
import gov.tna.discovery.taxonomy.common.repository.mongo.TrainingDocumentRepository;
import gov.tna.discovery.taxonomy.common.service.CategoriserService;
import gov.tna.discovery.taxonomy.common.service.domain.CategorisationResult;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ServiceConfigurationTest.class)
public class EvaluationServiceTest {
    // private static final Logger logger =
    // LoggerFactory.getLogger(CategoriserTest.class);

    @Autowired
    public EvaluationServiceImpl evaluationService;

    @Autowired
    TestDocumentRepository testDocumentRepository;

    @Autowired
    MongoTestDataSet mongoTestDataSet;

    @Autowired
    private LuceneTestDataSet luceneTestDataSet;

    // @Before
    public void initDataSet() throws IOException {
    }

    // @After
    public void emptyDataSet() throws IOException {
    }

    @Test
    public void testCreateTestDataSet() {
	mongoTestDataSet.initCategoryCollectionWith1element();

	assertThatTestDocDbIsEmpty();

	evaluationService.createTestDataset();

	assertThatTestDocDbContainsDocsWithLegacyCategories();
    }

    private void assertThatTestDocDbContainsDocsWithLegacyCategories() {
	assertThat(testDocumentRepository.count(), is(equalTo(120l)));
	Iterable<TestDocument> trainingDocuments = testDocumentRepository.findAll();
	assertThat(trainingDocuments, is(notNullValue()));
	TestDocument doc = trainingDocuments.iterator().next();
	assertThat(doc, is(notNullValue()));
	assertThat(doc.getLegacyCategories(), is(not(emptyArray())));
    }

    private void assertThatTestDocDbIsEmpty() {
	assertThat(testDocumentRepository.count(), is(equalTo(0l)));
    }

    @Test
    public void testEvaluateCategorisation() {
	// evaluationService.evaluateCategorisation();
	Assert.fail();
    }

    @Test
    public void testGetEvaluationReport() {
	// evaluationService.getEvaluationReport();
	Assert.fail();
    }

}

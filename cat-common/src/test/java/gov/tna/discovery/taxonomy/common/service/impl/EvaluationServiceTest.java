package gov.tna.discovery.taxonomy.common.service.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import gov.tna.discovery.taxonomy.common.config.ServiceConfigurationLightTest;
import gov.tna.discovery.taxonomy.common.config.ServiceConfigurationTest;
import gov.tna.discovery.taxonomy.common.mapper.TaxonomyMapper;
import gov.tna.discovery.taxonomy.common.repository.domain.lucene.InformationAssetView;
import gov.tna.discovery.taxonomy.common.repository.domain.mongo.TestDocument;
import gov.tna.discovery.taxonomy.common.repository.lucene.IAViewRepository;
import gov.tna.discovery.taxonomy.common.repository.lucene.LuceneTestDataSet;
import gov.tna.discovery.taxonomy.common.repository.mongo.CategoryRepository;
import gov.tna.discovery.taxonomy.common.repository.mongo.MongoConfigurationTest;
import gov.tna.discovery.taxonomy.common.repository.mongo.MongoTestDataSet;
import gov.tna.discovery.taxonomy.common.repository.mongo.TestDocumentRepository;
import gov.tna.discovery.taxonomy.common.service.CategoriserService;
import gov.tna.discovery.taxonomy.common.service.LegacySystemService;
import gov.tna.discovery.taxonomy.common.service.domain.CategorisationResult;
import gov.tna.discovery.taxonomy.common.service.domain.PaginatedList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ServiceConfigurationTest.class)
public class EvaluationServiceTest {
    // private static final Logger logger =
    // LoggerFactory.getLogger(CategoriserTest.class);

    public EvaluationServiceImpl evaluationService;

    @Autowired
    TestDocumentRepository testDocumentRepository;

    @Autowired
    MongoTestDataSet mongoTestDataSet;

    @Autowired
    private LuceneTestDataSet luceneTestDataSet;

    @Autowired
    private CategoryRepository categoryRepository;

    private String[] legacyCategories = new String[] { "Construction industries", "Labour" };

    @Before
    public void initMocks() throws IOException {
	evaluationService = new EvaluationServiceImpl();

	evaluationService.setLegacySystemService(getLegacySystemServiceMock());

	evaluationService.setIaviewRepository(getIaViewRepositoryMock());

	evaluationService.setCategoryRepository(categoryRepository);
	evaluationService.setTestDocumentRepository(testDocumentRepository);
    }

    @After
    public void dropDatabse() {
	mongoTestDataSet.dropDatabase();
    }

    @Test
    public void testCreateTestDataSet() {
	mongoTestDataSet.initCategoryCollectionWith1element();
	assertThatTestDocDbIsEmpty();

	evaluationService.createEvaluationTestDataset();

	assertThatTestDocDbContainsDocsWithLegacyCategories();
    }

    private void assertThatTestDocDbContainsDocsWithLegacyCategories() {
	assertThat(testDocumentRepository.count(), is(equalTo(1l)));
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
    public void testRunCategorisationOnTestDataSet() {
	initTestDocumentRepositoryWithOneElementWithLegacyCategories();
	evaluationService.setCategoriserService(getCategoriserServiceMock());

	evaluationService.runCategorisationOnTestDataSet();

	assertThatTestDocumentContainsCurrentSystemCategories();
    }

    private void assertThatTestDocumentContainsCurrentSystemCategories() {
	assertThat(testDocumentRepository.count(), is(equalTo(1l)));
	TestDocument doc = testDocumentRepository.findAll().iterator().next();
	assertThat(doc, notNullValue());
	assertThat(doc.getCategories(), notNullValue());
	assertThat(doc.getCategories(), is(not(emptyArray())));

    }

    private void initTestDocumentRepositoryWithOneElementWithLegacyCategories() {
	TestDocument testDocument = TaxonomyMapper.getTestDocumentFromIAView(MongoTestDataSet.getIAViewSample());
	testDocument.setLegacyCategories(legacyCategories);
	testDocumentRepository.save(testDocument);

    }

    @Test
    @Ignore
    public void testGetEvaluationReport() {
	// evaluationService.getEvaluationReport();
	Assert.fail();
    }

    private CategoriserService getCategoriserServiceMock() {
	CategoriserService categoriserService = Mockito.mock(CategoriserService.class);
	List<CategorisationResult> categorisationResults = new ArrayList<CategorisationResult>();
	categorisationResults.addAll(Arrays.asList(new CategorisationResult("Labour", 1.12f, 10),
		new CategorisationResult("Forestry", 1.02f, 15), new CategorisationResult("Forestry", 0.12f, 2)));
	Mockito.when(categoriserService.testCategoriseSingle(Mockito.any(InformationAssetView.class))).thenReturn(
		categorisationResults);

	return categoriserService;
    }

    private IAViewRepository getIaViewRepositoryMock() {
	IAViewRepository iaViewRepositoryMock = Mockito.mock(IAViewRepository.class);
	PaginatedList<InformationAssetView> searchResult = new PaginatedList<InformationAssetView>();
	searchResult.setNumberOfResults(1);
	searchResult.setResults(Arrays.asList(MongoTestDataSet.getIAViewSample()));
	Mockito.when(
		iaViewRepositoryMock.performSearch(Mockito.anyString(), Mockito.anyDouble(), Mockito.anyInt(),
			Mockito.anyInt())).thenReturn(searchResult);
	return iaViewRepositoryMock;
    }

    private LegacySystemService getLegacySystemServiceMock() {
	LegacySystemService legacySystemServiceMock = Mockito.mock(LegacySystemService.class);
	Mockito.when(legacySystemServiceMock.getLegacyCategoriesForCatDocRef(Mockito.anyString())).thenReturn(
		legacyCategories);
	return legacySystemServiceMock;
    }

}

package gov.tna.discovery.taxonomy.common.service.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import gov.tna.discovery.taxonomy.common.config.ServiceConfigurationTest;
import gov.tna.discovery.taxonomy.common.mapper.LuceneTaxonomyMapper;
import gov.tna.discovery.taxonomy.common.mapper.TaxonomyMapper;
import gov.tna.discovery.taxonomy.common.repository.domain.lucene.InformationAssetView;
import gov.tna.discovery.taxonomy.common.repository.domain.mongo.Category;
import gov.tna.discovery.taxonomy.common.repository.domain.mongo.CategoryEvaluationResult;
import gov.tna.discovery.taxonomy.common.repository.domain.mongo.EvaluationReport;
import gov.tna.discovery.taxonomy.common.repository.domain.mongo.TestDocument;
import gov.tna.discovery.taxonomy.common.repository.lucene.IAViewRepository;
import gov.tna.discovery.taxonomy.common.repository.lucene.LuceneTestDataSet;
import gov.tna.discovery.taxonomy.common.repository.mongo.CategoryRepository;
import gov.tna.discovery.taxonomy.common.repository.mongo.EvaluationReportRepository;
import gov.tna.discovery.taxonomy.common.repository.mongo.MongoTestDataSet;
import gov.tna.discovery.taxonomy.common.repository.mongo.TestDocumentRepository;
import gov.tna.discovery.taxonomy.common.service.CategoriserService;
import gov.tna.discovery.taxonomy.common.service.LegacySystemService;
import gov.tna.discovery.taxonomy.common.service.domain.TSetBasedCategorisationResult;
import gov.tna.discovery.taxonomy.common.service.domain.PaginatedList;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SuppressWarnings("rawtypes")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ServiceConfigurationTest.class)
public class EvaluationServiceTest {
    // private static final Logger logger =
    // LoggerFactory.getLogger(EvaluationServiceTest.class);

    public EvaluationServiceImpl evaluationService;

    @Autowired
    TestDocumentRepository testDocumentRepository;

    @Autowired
    private EvaluationReportRepository evaluationReportRepository;

    @Autowired
    MongoTestDataSet mongoTestDataSet;

    @Autowired
    private LuceneTestDataSet luceneTestDataSet;

    @Autowired
    private CategoryRepository categoryRepository;

    private String[] legacyCategories = new String[] { "Construction industries", "Labour" };

    private static final String CAT_BAD_ACCURACY = "Category with Bad Accuracy";
    private static final String CAT_MEDIUM_RECALL = "Category with Bad Recall";
    private static final String CAT_GOOD = "Category with Good Accuracy and Recall";
    private static final String CAT_FROM_LEGACY_SYSTEM_NOT_KNOWN = "Category Not known in new System";
    private static final String CAT_FROM_CURRENT_SYSTEM_NOT_FOUND = "Category from current system not Found anywhere";

    @Before
    public void initMocks() throws IOException {
	evaluationService = new EvaluationServiceImpl();

	evaluationService.setLegacySystemService(getLegacySystemServiceMock());

	evaluationService.setIaviewRepository(getIaViewRepositoryMock());

	evaluationService.setCategoryRepository(categoryRepository);
	evaluationService.setTestDocumentRepository(testDocumentRepository);
	evaluationService.setCategoriserService(null);
	evaluationService.setEvaluationReportRepository(evaluationReportRepository);
    }

    @After
    public void dropDatabse() {
	mongoTestDataSet.dropDatabase();
    }

    /**
     * TEST CREATE TEST DATASET
     */

    @Test
    public void testCreateTestDataSet() {
	mongoTestDataSet.initCategoryCollectionWith1element();
	assertThatTestDocDbIsEmpty();

	evaluationService.createEvaluationTestDataset(10);

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

    /**
     * TEST RUN CATEGORISATION ON TEST DATASET
     */

    @Test
    public void testRunCategorisationOnTestDataSet() {
	initTestDocumentRepositoryWithOneElementWithLegacyCategories();
	evaluationService.setCategoriserService(getCategoriserServiceMock());

	evaluationService.runCategorisationOnTestDataSet(true);

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

    /**
     * TEST EVALUATION REPORT
     */

    @Test
    public void testGetEvaluationReport() {
	initCategoryCollection();
	initTestDocumentRepositoryWithSeveralCompleteDocs();

	EvaluationReport report = evaluationService.getEvaluationReport("test report");

	assertThat(report, notNullValue());
	assertThat(report.getResults(), is(notNullValue()));
	assertThat(report.getResults(), is(not(empty())));
	assertThat("there should be 4 category evaluation results", report.getResults().size(), is(equalTo(5)));
	assertThat("there should be 3 documents processed", report.getNumberOfDocuments(), is(equalTo(3)));

	DecimalFormat df = new DecimalFormat("#.####");

	assertThat(report.getAvgRecall(), is(notNullValue()));
	assertThat("Global recall should be 8/15", df.format(report.getAvgRecall()),
		is(equalTo(df.format(1.0d * 8 / 15))));
	assertThat(report.getAvgAccuracy(), is(notNullValue()));
	assertThat("Global accuracy should be 7/15", df.format(report.getAvgAccuracy()),
		is(equalTo(df.format(1.0d * 7 / 15))));
	assertThat(evaluationReportRepository.count(), is(equalTo(1l)));

	int numNotFoundInCatRepo = 0;
	int numNotFoundInTDocCat = 0;
	int numNotFoundInTDocLegacyCat = 0;
	for (CategoryEvaluationResult categoryEvaluationResult : report.getResults()) {
	    if (!categoryEvaluationResult.isFoundInCatRepo()) {
		numNotFoundInCatRepo++;
	    }
	    if (!categoryEvaluationResult.isFoundInTDocCat()) {
		numNotFoundInTDocCat++;
	    }
	    if (!categoryEvaluationResult.isFoundInTDocLegacyCat()) {
		numNotFoundInTDocLegacyCat++;
	    }
	}
	assertThat(numNotFoundInCatRepo, is(equalTo(1)));
	assertThat(numNotFoundInTDocCat, is(equalTo(2)));
	assertThat(numNotFoundInTDocLegacyCat, is(equalTo(1)));
    }

    private void initCategoryCollection() {
	saveCategory(CAT_BAD_ACCURACY);
	saveCategory(CAT_MEDIUM_RECALL);
	saveCategory(CAT_GOOD);
	saveCategory(CAT_FROM_CURRENT_SYSTEM_NOT_FOUND);

    }

    private void saveCategory(String categoryTtl) {
	Category category = new Category();
	category.setCiaid(categoryTtl);
	category.setTtl(categoryTtl);
	category.setLck(false);
	category.setSc(0.0);
	categoryRepository.save(category);
    }

    private void initTestDocumentRepositoryWithSeveralCompleteDocs() {
	TestDocument testDocument1 = TaxonomyMapper.getTestDocumentFromIAView(MongoTestDataSet.getIAViewSample());
	testDocument1.setDocReference("DOC1");
	testDocument1.setCategories(new String[] { CAT_BAD_ACCURACY, CAT_MEDIUM_RECALL });
	testDocument1.setLegacyCategories(new String[] { CAT_BAD_ACCURACY, CAT_MEDIUM_RECALL });
	testDocumentRepository.save(testDocument1);

	TestDocument testDocument2 = TaxonomyMapper.getTestDocumentFromIAView(MongoTestDataSet.getIAViewSample());
	testDocument2.setDocReference("DOC2");
	testDocument2.setCategories(new String[] { CAT_BAD_ACCURACY, CAT_MEDIUM_RECALL });
	testDocument2.setLegacyCategories(new String[] { CAT_MEDIUM_RECALL });
	testDocumentRepository.save(testDocument2);

	TestDocument testDocument3 = TaxonomyMapper.getTestDocumentFromIAView(MongoTestDataSet.getIAViewSample());
	testDocument3.setDocReference("DOC3");
	testDocument3.setCategories(new String[] { CAT_BAD_ACCURACY, CAT_GOOD });
	testDocument3
		.setLegacyCategories(new String[] { CAT_MEDIUM_RECALL, CAT_GOOD, CAT_FROM_LEGACY_SYSTEM_NOT_KNOWN });
	testDocumentRepository.save(testDocument3);

    }

    /**
     * 
     * MOCKS
     * 
     */

    private CategoriserService getCategoriserServiceMock() {
	CategoriserService categoriserService = Mockito.mock(CategoriserService.class);
	List<TSetBasedCategorisationResult> categorisationResults = new ArrayList<TSetBasedCategorisationResult>();
	categorisationResults.addAll(Arrays.asList(new TSetBasedCategorisationResult("Labour", 1.12f, 10),
		new TSetBasedCategorisationResult("Forestry", 1.02f, 15), new TSetBasedCategorisationResult("Forestry",
			0.12f, 2)));
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

	Mockito.when(
		iaViewRepositoryMock.searchIAViewIndexByFieldAndPhrase(Mockito.anyString(), Mockito.anyString(),
			Mockito.anyInt())).thenReturn(new TopDocs(1, new ScoreDoc[] { new ScoreDoc(0, 1f) }, 1f));

	Mockito.when(iaViewRepositoryMock.getDoc(Mockito.any(ScoreDoc.class))).thenReturn(
		LuceneTaxonomyMapper.getLuceneDocumentFromIAView(MongoTestDataSet.getIAViewSample()));
	return iaViewRepositoryMock;
    }

    private LegacySystemService getLegacySystemServiceMock() {
	LegacySystemService legacySystemServiceMock = Mockito.mock(LegacySystemService.class);
	HashMap<String, String[]> mapOfLegacyDocuments = new HashMap<String, String[]>();
	mapOfLegacyDocuments.put("C465432", new String[] { "Air Force", "Medals" });
	Mockito.when(legacySystemServiceMock.findLegacyDocumentsByCategory(Mockito.anyString(), Mockito.anyInt()))
		.thenReturn(mapOfLegacyDocuments);
	return legacySystemServiceMock;
    }

}

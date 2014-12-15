package gov.tna.discovery.taxonomy.common.service.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import gov.tna.discovery.taxonomy.common.config.ServiceConfigurationLightTest;
import gov.tna.discovery.taxonomy.common.config.ServiceConfigurationTest;
import gov.tna.discovery.taxonomy.common.repository.domain.lucene.InformationAssetView;
import gov.tna.discovery.taxonomy.common.repository.domain.mongo.TestDocument;
import gov.tna.discovery.taxonomy.common.repository.lucene.IAViewRepository;
import gov.tna.discovery.taxonomy.common.repository.lucene.LuceneTestDataSet;
import gov.tna.discovery.taxonomy.common.repository.mongo.CategoryRepository;
import gov.tna.discovery.taxonomy.common.repository.mongo.MongoConfigurationTest;
import gov.tna.discovery.taxonomy.common.repository.mongo.MongoTestDataSet;
import gov.tna.discovery.taxonomy.common.repository.mongo.TestDocumentRepository;
import gov.tna.discovery.taxonomy.common.service.LegacySystemService;
import gov.tna.discovery.taxonomy.common.service.domain.PaginatedList;

import java.io.IOException;
import java.util.Arrays;

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
@SpringApplicationConfiguration(classes = ServiceConfigurationLightTest.class)
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

    @Before
    public void initMocks() throws IOException {
	evaluationService = new EvaluationServiceImpl();

	LegacySystemService legacySystemServiceMock = Mockito.mock(LegacySystemService.class);
	Mockito.when(legacySystemServiceMock.getLegacyCategoriesForCatDocRef(Mockito.anyString())).thenReturn(
		new String[] { "Labour", });
	evaluationService.setLegacySystemService(legacySystemServiceMock);

	IAViewRepository iaViewRepositoryMock = Mockito.mock(IAViewRepository.class);
	PaginatedList<InformationAssetView> searchResult = new PaginatedList<InformationAssetView>();
	searchResult.setNumberOfResults(1);
	InformationAssetView iaView = new InformationAssetView();
	iaView.setCATDOCREF("HLG 102/182");
	iaView.setCONTEXTDESCRIPTION("Ministry of Health and successors: Miscellaneous Registered Files (99,000 Series). Building and Civil Engineering: National Programme.");
	iaView.setCOVERINGDATES("1952");
	iaView.setTITLE("Labour requirements for the housing programme.");
	iaView.setDESCRIPTION("Labour requirements for the housing programme.");
	iaView.setDOCREFERENCE("C1330010");
	searchResult.setResults(Arrays.asList(iaView));
	Mockito.when(
		iaViewRepositoryMock.performSearch(Mockito.anyString(), Mockito.anyDouble(), Mockito.anyInt(),
			Mockito.anyInt())).thenReturn(searchResult);
	evaluationService.setIaviewRepository(iaViewRepositoryMock);

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
    @Ignore
    public void testEvaluateCategorisation() {
	// evaluationService.evaluateCategorisation();
	Assert.fail();
    }

    @Test
    @Ignore
    public void testGetEvaluationReport() {
	// evaluationService.getEvaluationReport();
	Assert.fail();
    }

}

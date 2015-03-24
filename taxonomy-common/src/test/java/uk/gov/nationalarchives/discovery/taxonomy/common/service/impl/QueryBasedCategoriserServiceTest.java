package uk.gov.nationalarchives.discovery.taxonomy.common.service.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.gov.nationalarchives.discovery.taxonomy.common.config.ServiceConfigurationTest;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetView;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.Category;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.IAViewUpdate;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.MongoInformationAssetView;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.CategorisationResult;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.IAViewRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.InMemoryIAViewRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.CategoryRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.IAViewUpdateRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.InformationAssetViewMongoRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.MongoTestDataSet;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.solr.SolrTaxonomyIAViewRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.CategoriserService;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.async.AsyncQueryBasedTaskManager;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("queryBased")
@SpringApplicationConfiguration(classes = ServiceConfigurationTest.class)
@SuppressWarnings("unchecked")
public class QueryBasedCategoriserServiceTest {

    @Autowired
    @SuppressWarnings("rawtypes")
    CategoriserService categoriserService;

    @Autowired
    MongoTestDataSet mongoTestDataSet;

    @Value("${lucene.index.version}")
    private String luceneVersion;

    @Before
    public void initDataSet() throws IOException {
	mongoTestDataSet.initCategoryCollection();
    }

    @After
    public void emptyDataSet() throws IOException {
	mongoTestDataSet.dropDatabase();
    }

    @Test
    public void testTestCategoriseSingle() {
	InformationAssetView iaView = new InformationAssetView();
	iaView.setCATDOCREF("AIR 37/177");
	iaView.setCONTEXTDESCRIPTION("Air Ministry: Allied Expeditionary Air Force, later Supreme Headquarters Allied Expeditionary Force (Air), and 2nd Tactical Air Force: Registered Files and Reports.");
	iaView.setCOVERINGDATES("1942");
	iaView.setDESCRIPTION("CHIEF OF STAFF, SUPREME ALLIED COMMAND: Operation \"Round-up\": operational organisation of RAF.");
	iaView.setDOCREFERENCE("C508096");
	iaView.setTITLE("CHIEF OF STAFF, SUPREME ALLIED COMMAND: Operation \"Round-up\": operational organisation of RAF");

	List<CategorisationResult> categorisationResults = categoriserService.testCategoriseSingle("C508096");
	assertThat(categorisationResults, is(notNullValue()));
	assertThat(categorisationResults, is(not(empty())));
	assertThat(categorisationResults.get(0).getName(), is(equalTo("Fishing")));

    }

    @Test
    public void testCategoriseSingle() {
	InformationAssetViewMongoRepository informationAssetViewMongoRepositoryMock = Mockito
		.mock(InformationAssetViewMongoRepository.class);
	IAViewUpdateRepository iaViewUpdateRepositoryMock = Mockito.mock(IAViewUpdateRepository.class);

	QueryBasedCategoriserServiceImpl categoriserService = new QueryBasedCategoriserServiceImpl(
		Mockito.mock(CategoryRepository.class), createIaViewRepositoryMock(),
		createInMemoryIAViewRepositoryMock(), informationAssetViewMongoRepositoryMock,
		iaViewUpdateRepositoryMock, Mockito.mock(AsyncQueryBasedTaskManager.class),
		Mockito.mock(SolrTaxonomyIAViewRepository.class));

	List<CategorisationResult> categorisationResults = categoriserService.categoriseSingle("C508096");
	assertThat(categorisationResults, is(notNullValue()));
	assertThat(categorisationResults, is(not(empty())));
	assertThat(categorisationResults.get(0).getName(), is(equalTo("Fishing")));

	Mockito.verify(informationAssetViewMongoRepositoryMock, Mockito.times(1)).save(
		Mockito.any(MongoInformationAssetView.class));
	Mockito.verify(iaViewUpdateRepositoryMock, Mockito.times(1)).save(Mockito.any(IAViewUpdate.class));

    }

    private InMemoryIAViewRepository createInMemoryIAViewRepositoryMock() {
	InMemoryIAViewRepository inMemoryIAViewRepository = Mockito.mock(InMemoryIAViewRepository.class);
	Category category = new Category();
	category.setTtl("Fishing");
	category.setSc(0d);
	Mockito.when(
		inMemoryIAViewRepository.findRelevantCategoriesForDocument(Mockito.any(InformationAssetView.class),
			Mockito.any(Iterable.class))).thenReturn(Arrays.asList(category));
	return inMemoryIAViewRepository;
    }

    private IAViewRepository createIaViewRepositoryMock() {
	IAViewRepository iaViewRepositoryMock = Mockito.mock(IAViewRepository.class);
	InformationAssetView iaView = new InformationAssetView();
	iaView.setCATDOCREF("AIR 37/177");
	iaView.setCONTEXTDESCRIPTION("Air Ministry: Allied Expeditionary Air Force, later Supreme Headquarters Allied Expeditionary Force (Air), and 2nd Tactical Air Force: Registered Files and Reports.");
	iaView.setCOVERINGDATES("1942");
	iaView.setDESCRIPTION("CHIEF OF STAFF, SUPREME ALLIED COMMAND: Operation \"Round-up\": operational organisation of RAF.");
	iaView.setDOCREFERENCE("C508096");
	iaView.setTITLE("CHIEF OF STAFF, SUPREME ALLIED COMMAND: Operation \"Round-up\": operational organisation of RAF");
	Mockito.when(iaViewRepositoryMock.searchDocByDocReference(Mockito.anyString())).thenReturn(iaView);
	return iaViewRepositoryMock;
    }
}

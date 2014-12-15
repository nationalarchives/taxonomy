package gov.tna.discovery.taxonomy.ws.controller;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import gov.tna.discovery.taxonomy.common.repository.domain.TrainingDocument;
import gov.tna.discovery.taxonomy.common.repository.domain.lucene.InformationAssetView;
import gov.tna.discovery.taxonomy.common.repository.domain.mongo.Category;
import gov.tna.discovery.taxonomy.common.repository.lucene.IAViewRepository;
import gov.tna.discovery.taxonomy.common.repository.mongo.CategoryRepository;
import gov.tna.discovery.taxonomy.common.repository.mongo.MongoTestDataSet;
import gov.tna.discovery.taxonomy.common.repository.mongo.TrainingDocumentRepository;
import gov.tna.discovery.taxonomy.common.service.domain.PaginatedList;
import gov.tna.discovery.taxonomy.common.service.exception.TaxonomyErrorType;
import gov.tna.discovery.taxonomy.ws.domain.PublishRequest;
import gov.tna.discovery.taxonomy.ws.domain.SearchIAViewRequest;
import gov.tna.discovery.taxonomy.ws.domain.TaxonomyErrorResponse;
import gov.tna.discovery.taxonomy.ws.domain.TestCategoriseSingleRequest;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WSApplicationTest.class)
@WebAppConfiguration
@IntegrationTest({ "server.port=8085", "management.port=8085" })
public class TaxonomyControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(TaxonomyControllerTest.class);

    private static final String WS_URL = "http://localhost:8085/";

    private static final String WS_PATH_HEALTH = "health";

    private static final String WS_PATH_SEARCH = "taxonomy/search";

    private static final String WS_PATH_PUBLISH = "taxonomy/publish";

    // private static final Logger logger =
    // LoggerFactory.getLogger(TestTaxonomyController.class);

    private static final String TEST_CATEGORY_CIAID = "C10052";

    private static final String WS_PATH_TEST_CATEGORISE_SINGLE = "taxonomy/testCategoriseSingle";

    RestTemplate restTemplate = new TestRestTemplate();

    @Autowired
    CategoryRepository catRepo;

    @Autowired
    TrainingDocumentRepository trainingDocRepo;

    @Autowired
    IAViewRepository iaViewRepository;

    @Autowired
    MongoTestDataSet mongoTestDataSet;

    @Before
    public void initDataSet() throws IOException {
	mongoTestDataSet.initCategoryCollection();
	mongoTestDataSet.initTrainingSetCollection();
    }

    @After
    public void emptyDataSet() throws IOException {
	mongoTestDataSet.dropDatabase();
    }

    @Test
    public final void testWSisUp() {
	String responseBody = restTemplate.getForEntity(WS_URL + WS_PATH_HEALTH, String.class).getBody();
	assertThat(responseBody, containsString("UP"));
    }

    @Test
    public final void testSearchIaView() {
	SearchIAViewRequest request = new SearchIAViewRequest();
	request.setCategoryQuery("*record*");
	request.setLimit(10);
	PaginatedList iaViews = restTemplate.postForObject(WS_URL + WS_PATH_SEARCH, request, PaginatedList.class);
	assertThat(iaViews, is(notNullValue()));
	assertThat(iaViews.getResults(), is(notNullValue()));
	assertThat(iaViews.size(), is(not(equalTo(0))));
    }

    @Test
    public final void testPublicationOKandRunning() throws InterruptedException {
	Category category = catRepo.findByCiaid(TEST_CATEGORY_CIAID);

	ResponseEntity<String> response = doPublishPostRequestOnWS(category);

	assertThat(response.getBody(), is(notNullValue()));
	assertThat(response.getBody(), containsString("OK"));
    }

    @Test
    public final void testPublicationWasSuccesful() throws InterruptedException {
	Category category = catRepo.findByCiaid(TEST_CATEGORY_CIAID);

	doPublishPostRequestOnWS(category);

	// Thread.sleep(5000);
	waitForAsyncPublicationToBeCompleted();

	List<TrainingDocument> trainingDocs = trainingDocRepo.findByCategory(category.getTtl());
	assertThat(trainingDocs, is(notNullValue()));
	assertThat(trainingDocs, is(not(empty())));
	assertThat(trainingDocs.size(), equalTo(4));

	PaginatedList<InformationAssetView> IAViewResults = iaViewRepository.performSearch(category.getQry(),
		(category.getSc()), 1000, 0);
	assertThat(IAViewResults, is(notNullValue()));
	assertThat(IAViewResults.getResults(), is(notNullValue()));
	assertThat(IAViewResults.getResults(), is(not(empty())));
	assertThat(IAViewResults.size(), equalTo(4));

	logger.debug("Publication succeeded");

    }

    @Test
    public final void testPublicationFailedBecauseOfLock() {
	String ciaid = getLockedCategoryId();

	PublishRequest publishRequest = new PublishRequest(ciaid);
	ResponseEntity<TaxonomyErrorResponse> response = restTemplate.postForEntity(WS_URL + WS_PATH_PUBLISH,
		publishRequest, TaxonomyErrorResponse.class);

	assertThat(response.getBody(), is(notNullValue()));
	assertThat(response.getBody().getError(), equalTo(TaxonomyErrorType.LOCKED_CATEGORY));

    }

    @Test
    public final void testPublicationFailedBecauseOfInvalidQuery() {

	String ciaid = getCategoryIdWithInvalidQuery();

	PublishRequest publishRequest = new PublishRequest(ciaid);
	ResponseEntity<TaxonomyErrorResponse> response = restTemplate.postForEntity(WS_URL + WS_PATH_PUBLISH,
		publishRequest, TaxonomyErrorResponse.class);

	assertThat(response.getBody(), is(notNullValue()));
	assertThat(response.getBody().getError(), equalTo(TaxonomyErrorType.INVALID_CATEGORY_QUERY));
    }

    @Test
    public final void testTestCategoriseSingleDocumentWithMissingDescription() {
	TestCategoriseSingleRequest request = new TestCategoriseSingleRequest();
	request.setTitle("TRINITY Church of England School.");
	ResponseEntity<TaxonomyErrorResponse> response = restTemplate.postForEntity(WS_URL
		+ WS_PATH_TEST_CATEGORISE_SINGLE, request, TaxonomyErrorResponse.class);

	assertThat(response.getBody(), is(notNullValue()));
	assertThat(response.getBody().getError(), equalTo(TaxonomyErrorType.INVALID_PARAMETER));

    }

    @SuppressWarnings("unchecked")
    @Test
    public final void testTestCategoriseSingleDocumentWithOnlyDescription() {
	TestCategoriseSingleRequest request = new TestCategoriseSingleRequest();
	request.setDescription("UK bilateral aid programme: review by Ministry of Overseas Development working party; papers, minutes and correspondance");
	List<LinkedHashMap<String, String>> categorisationResults = restTemplate.postForObject(WS_URL
		+ WS_PATH_TEST_CATEGORISE_SINGLE, request, List.class);

	assertThat(categorisationResults, is(notNullValue()));
	assertThat(categorisationResults, is(not(empty())));
	assertThat(categorisationResults.get(0).get("name"), not(isEmptyString()));
	logger.info(Arrays.toString(categorisationResults.toArray()));

    }

    private void waitForAsyncPublicationToBeCompleted() throws InterruptedException {
	Category category;
	category = catRepo.findByCiaid(TEST_CATEGORY_CIAID);
	while (category.getLck() == true) {
	    Thread.sleep(500);
	    category = catRepo.findByCiaid(TEST_CATEGORY_CIAID);
	}
    }

    private ResponseEntity<String> doPublishPostRequestOnWS(Category category) {
	PublishRequest publishRequest = new PublishRequest(category.getCiaid());
	return restTemplate.postForEntity(WS_URL + WS_PATH_PUBLISH, publishRequest, String.class);
    }

    private String getLockedCategoryId() {
	Category category = catRepo.findByCiaid(TEST_CATEGORY_CIAID);
	category.setLck(true);
	catRepo.save(category);
	return category.getCiaid();
    }

    private String getCategoryIdWithInvalidQuery() {
	Category category = catRepo.findByCiaid(TEST_CATEGORY_CIAID);
	category.setQry("\"venereal disease\" OR");
	catRepo.save(category);
	String ciaid = category.getCiaid();
	return ciaid;
    }
}

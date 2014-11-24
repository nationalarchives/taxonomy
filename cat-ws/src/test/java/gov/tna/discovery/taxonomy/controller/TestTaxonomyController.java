package gov.tna.discovery.taxonomy.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import gov.tna.discovery.taxonomy.WSApplication;
import gov.tna.discovery.taxonomy.domain.PublishRequest;
import gov.tna.discovery.taxonomy.domain.SearchIAViewRequest;
import gov.tna.discovery.taxonomy.domain.TaxonomyErrorResponse;
import gov.tna.discovery.taxonomy.domain.TestCategoriseSingleRequest;
import gov.tna.discovery.taxonomy.repository.domain.TrainingDocument;
import gov.tna.discovery.taxonomy.repository.domain.lucene.InformationAssetView;
import gov.tna.discovery.taxonomy.repository.domain.mongo.Category;
import gov.tna.discovery.taxonomy.repository.lucene.IAViewRepository;
import gov.tna.discovery.taxonomy.repository.mongo.CategoryRepository;
import gov.tna.discovery.taxonomy.repository.mongo.TrainingDocumentRepository;
import gov.tna.discovery.taxonomy.service.exception.TaxonomyErrorType;

import java.util.LinkedHashMap;
import java.util.List;

import org.junit.After;
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
@SpringApplicationConfiguration(classes = WSApplication.class)
@WebAppConfiguration
@IntegrationTest({ "server.port=8085", "management.port=8085" })
public class TestTaxonomyController {

    private static final Logger logger = LoggerFactory.getLogger(TestTaxonomyController.class);

    private static final String WS_URL = "http://localhost:8085/";

    private static final String WS_PATH_HEALTH = "health";

    private static final String WS_PATH_SEARCH = "taxonomy/search";

    private static final String WS_PATH_PUBLISH = "taxonomy/publish";

    // private static final Logger logger =
    // LoggerFactory.getLogger(TestTaxonomyController.class);

    private static final String DISEASE_CATEGORY_CIAID = "C10032";

    private static final String WS_PATH_TEST_CATEGORISE_SINGLE = "taxonomy/testCategoriseSingle";

    RestTemplate restTemplate = new TestRestTemplate();

    @Autowired
    CategoryRepository catRepo;

    @Autowired
    TrainingDocumentRepository trainingDocRepo;

    @Autowired
    IAViewRepository iaViewRepository;

    // @Autowired
    // MongoTestDataSet mongoTestDataSet;
    //
    // @Before
    // public void initDataSet() throws IOException {
    // mongoTestDataSet.initCategoryCollection();
    // mongoTestDataSet.initTrainingSetCollection();
    // }
    //
    // @After
    // public void emptyDataSet() throws IOException {
    // mongoTestDataSet.dropDatabase();
    // }

    @After
    public void resetDataSet() {
	Category category = catRepo.findByCiaid(DISEASE_CATEGORY_CIAID);
	category.setLck(false);
	category.setQry("\"venereal disease\" OR \"tropical disease\" OR \"industrial disease\" OR \"infectious disease\" OR \"bubonic plague\" OR \"yellow fever\" OR \"malaria\" OR \"tuberculosis\" OR \"scurvy\" OR \"rickets\" OR \"measles\" OR \"influenza\" OR \"bronchitis\" OR \"pneumoconiosis\" OR \"emphysema\" OR \"byssinosis\" OR \"polio\" OR \"dengue fever\" OR \"rabies\" OR \"swine fever\" OR \"weils disease\" OR \"cancer\" OR \"asthma\" OR \"syphilis\" OR \"typhoid\" OR \"gonorrhoea\" OR \"smallpox\" OR \"cholera\" OR \"cholera morbus\" OR \"typhus\" OR \"meningitis\" OR \"dysentery\" OR \"scarlatina\" OR \"scarlet fever\" OR \"pneumonia\" OR \"cynanche tonsillaris\" OR \"synocha\" OR \"opthalmia\" OR \"whooping cough\" OR \"HIV\" OR \"asbestosis\" OR \"mesothelioma\" OR \"beri beri\" OR \"multiple sclerosis\" OR \"diabetes\" OR \"leus venerea\" OR \"leprosy\" OR \"poliomyelitis\" OR \"encephalitis\" OR \"Trypanosomiasis\"");
	catRepo.save(category);

    }

    @Test
    public final void testWSisUp() {
	String responseBody = restTemplate.getForEntity(WS_URL + WS_PATH_HEALTH, String.class).getBody();
	assertThat(responseBody, containsString("UP"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public final void testSearchIaView() {
	SearchIAViewRequest request = new SearchIAViewRequest();
	request.setCategoryQuery("\"venereal disease\" OR \"tropical disease\" OR \"industrial disease\" OR \"infectious disease\" OR \"bubonic plague\" OR \"yellow fever\" OR \"malaria\" OR \"tuberculosis\" OR \"scurvy\" OR \"rickets\" OR \"measles\" OR \"influenza\" OR \"bronchitis\" OR \"pneumoconiosis\" OR \"emphysema\" OR \"byssinosis\" OR \"polio\" OR \"dengue fever\" OR \"rabies\" OR \"swine fever\" OR \"weils disease\" OR \"cancer\" OR \"asthma\" OR \"syphilis\" OR \"typhoid\" OR \"gonorrhoea\" OR \"smallpox\" OR \"cholera\" OR \"cholera morbus\" OR \"typhus\" OR \"meningitis\" OR \"dysentery\" OR \"scarlatina\" OR \"scarlet fever\" OR \"pneumonia\" OR \"cynanche tonsillaris\" OR \"synocha\" OR \"opthalmia\" OR \"whooping cough\" OR \"HIV\" OR \"asbestosis\" OR \"mesothelioma\" OR \"beri beri\" OR \"multiple sclerosis\" OR \"diabetes\" OR \"leus venerea\" OR \"leprosy\" OR \"poliomyelitis\" OR \"encephalitis\" OR \"Trypanosomiasis\"");
	request.setLimit(10);
	List<InformationAssetView> iaViews = restTemplate.postForEntity(WS_URL + WS_PATH_SEARCH, request, List.class)
		.getBody();
	assertThat(iaViews, is(notNullValue()));
	assertThat(iaViews, is(not(empty())));
    }

    @Test
    public final void testPublicationOKandRunning() throws InterruptedException {
	Category category = catRepo.findByCiaid(DISEASE_CATEGORY_CIAID);

	ResponseEntity<String> response = doPublishPostRequestOnWS(category);

	assertThat(response.getBody(), is(notNullValue()));
	assertThat(response.getBody(), containsString("OK"));
    }

    @Test
    public final void testPublicationWasSuccesful() throws InterruptedException {
	Category category = catRepo.findByCiaid(DISEASE_CATEGORY_CIAID);

	ResponseEntity<String> response = doPublishPostRequestOnWS(category);

	// Thread.sleep(5000);
	waitForAsyncPublicationToBeCompleted();

	List<TrainingDocument> trainingDocs = trainingDocRepo.findByCategory(category.getTtl());
	assertThat(trainingDocs, is(notNullValue()));
	assertThat(trainingDocs, is(not(empty())));
	assertThat(trainingDocs.size(), equalTo(17));

	List<InformationAssetView> IAViewResults = iaViewRepository.performSearch(category.getQry(),
		(category.getSc()), 1000, 0);
	assertThat(IAViewResults, is(notNullValue()));
	assertThat(IAViewResults, is(not(empty())));
	assertThat(IAViewResults.size(), equalTo(17));

	logger.debug("Publication successed");

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
	request.setDescription("Registration Sub-District: Greasley Civil Parish, Township or Place: Annesley Felley RD 429 RS 1 ED 1");
	List<Object> categoryRelevancies = restTemplate.postForEntity(WS_URL + WS_PATH_TEST_CATEGORISE_SINGLE, request,
		List.class).getBody();

	assertThat(categoryRelevancies, is(notNullValue()));
	assertThat(categoryRelevancies, is(not(empty())));
	LinkedHashMap<String, Object> firstElement = (LinkedHashMap<String, Object>) categoryRelevancies.get(0);
	String categoryName = (String) firstElement.get("name");
	assertThat(categoryName, is(notNullValue()));
	assertThat(categoryName, is(equalTo("Sewerage")));

    }

    private void waitForAsyncPublicationToBeCompleted() throws InterruptedException {
	Category category;
	category = catRepo.findByCiaid(DISEASE_CATEGORY_CIAID);
	while (category.getLck() == true) {
	    Thread.sleep(500);
	    category = catRepo.findByCiaid(DISEASE_CATEGORY_CIAID);
	}
    }

    private ResponseEntity<String> doPublishPostRequestOnWS(Category category) {
	PublishRequest publishRequest = new PublishRequest(category.getCiaid());
	return restTemplate.postForEntity(WS_URL + WS_PATH_PUBLISH, publishRequest, String.class);
    }

    private String getLockedCategoryId() {
	Category category = catRepo.findByCiaid(DISEASE_CATEGORY_CIAID);
	category.setLck(true);
	catRepo.save(category);
	return category.getCiaid();
    }

    private String getCategoryIdWithInvalidQuery() {
	Category category = catRepo.findByCiaid(DISEASE_CATEGORY_CIAID);
	category.setQry("\"venereal disease\" OR");
	catRepo.save(category);
	String ciaid = category.getCiaid();
	return ciaid;
    }
}

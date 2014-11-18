package gov.tna.discovery.taxonomy;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import gov.tna.discovery.taxonomy.domain.SearchIAViewRequest;
import gov.tna.discovery.taxonomy.domain.TaxonomyErrorResponse;
import gov.tna.discovery.taxonomy.repository.domain.lucene.InformationAssetView;
import gov.tna.discovery.taxonomy.repository.domain.mongo.Category;
import gov.tna.discovery.taxonomy.repository.mongo.CategoryRepository;
import gov.tna.discovery.taxonomy.service.exception.TaxonomyErrorType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

    private static final String WS_URL = "http://localhost:8085/";

    private static final String WS_PATH_HEALTH = "health";

    private static final String WS_PATH_SEARCH = "taxonomy/search";

    private static final String WS_PATH_PUBLISH = "taxonomy/publish?catId={catId}";

    private static final String PUBLISH_CAT_ID_PARAM = "catId";

    // private static final Logger logger =
    // LoggerFactory.getLogger(TestTaxonomyController.class);

    private static final String DISEASE_CATEGORY_ID = "541811223158321a80587e43";

    RestTemplate restTemplate = new TestRestTemplate();

    @Autowired
    CategoryRepository catRepo;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public final void testWSisUp() {
	String responseBody = restTemplate.getForEntity(WS_URL + WS_PATH_HEALTH, String.class).getBody();
	assertThat(responseBody, containsString("UP"));
    }

    @After
    public void resetDataSet() {
	Category category = catRepo.findOne(DISEASE_CATEGORY_ID);
	category.setLck(false);
	category.setQry("\"venereal disease\" OR \"tropical disease\" OR \"industrial disease\" OR \"infectious disease\" OR \"bubonic plague\" OR \"yellow fever\" OR \"malaria\" OR \"tuberculosis\" OR \"scurvy\" OR \"rickets\" OR \"measles\" OR \"influenza\" OR \"bronchitis\" OR \"pneumoconiosis\" OR \"emphysema\" OR \"byssinosis\" OR \"polio\" OR \"dengue fever\" OR \"rabies\" OR \"swine fever\" OR \"weils disease\" OR \"cancer\" OR \"asthma\" OR \"syphilis\" OR \"typhoid\" OR \"gonorrhoea\" OR \"smallpox\" OR \"cholera\" OR \"cholera morbus\" OR \"typhus\" OR \"meningitis\" OR \"dysentery\" OR \"scarlatina\" OR \"scarlet fever\" OR \"pneumonia\" OR \"cynanche tonsillaris\" OR \"synocha\" OR \"opthalmia\" OR \"whooping cough\" OR \"HIV\" OR \"asbestosis\" OR \"mesothelioma\" OR \"beri beri\" OR \"multiple sclerosis\" OR \"diabetes\" OR \"leus venerea\" OR \"leprosy\" OR \"poliomyelitis\" OR \"encephalitis\" OR \"Trypanosomiasis\"");
	catRepo.save(category);

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
    public final void testPublicationOKandRunning() {
	Category category = catRepo.findOne(DISEASE_CATEGORY_ID);

	Map<String, String> urlVariables = new HashMap<String, String>();
	urlVariables.put(PUBLISH_CAT_ID_PARAM, DISEASE_CATEGORY_ID);
	ResponseEntity<String> response = restTemplate.getForEntity(WS_URL + WS_PATH_PUBLISH, String.class,
		urlVariables);
	assertThat(response.getBody(), is(notNullValue()));
	assertThat(response.getBody(), containsString("OK"));
    }

    @Test
    public final void testPublicationFailedBecauseOfLock() {

	Category category = catRepo.findOne(DISEASE_CATEGORY_ID);
	category.setLck(true);
	catRepo.save(category);

	Map<String, String> urlVariables = new HashMap<String, String>();
	urlVariables.put(PUBLISH_CAT_ID_PARAM, DISEASE_CATEGORY_ID);
	ResponseEntity<TaxonomyErrorResponse> response = restTemplate.getForEntity(WS_URL + WS_PATH_PUBLISH,
		TaxonomyErrorResponse.class, urlVariables);
	assertThat(response.getBody(), is(notNullValue()));
	assertThat(response.getBody().getError(), equalTo(TaxonomyErrorType.LOCKED_CATEGORY));

    }

    @Test
    public final void testPublicationFailedBecauseOfInvalidQuery() {

	Category category = catRepo.findOne(DISEASE_CATEGORY_ID);
	category.setQry("\"venereal disease\" OR");
	catRepo.save(category);

	Map<String, String> urlVariables = new HashMap<String, String>();
	urlVariables.put(PUBLISH_CAT_ID_PARAM, DISEASE_CATEGORY_ID);
	ResponseEntity<TaxonomyErrorResponse> response = restTemplate.getForEntity(WS_URL + WS_PATH_PUBLISH,
		TaxonomyErrorResponse.class, urlVariables);
	assertThat(response.getBody(), is(notNullValue()));
	assertThat(response.getBody().getError(), equalTo(TaxonomyErrorType.INVALID_CATEGORY_QUERY));
    }
}

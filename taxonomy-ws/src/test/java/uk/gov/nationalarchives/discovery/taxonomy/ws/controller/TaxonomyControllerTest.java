/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.ws.controller;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.exception.TaxonomyErrorType;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.TrainingDocument;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetView;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.Category;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.PaginatedList;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.IAViewRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.IAViewRepositoryTest;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.CategoryRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.MongoTestDataSet;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.TrainingDocumentRepository;
import uk.gov.nationalarchives.discovery.taxonomy.ws.model.PublishRequest;
import uk.gov.nationalarchives.discovery.taxonomy.ws.model.SearchIAViewRequest;
import uk.gov.nationalarchives.discovery.taxonomy.ws.model.TaxonomyErrorResponse;
import uk.gov.nationalarchives.discovery.taxonomy.ws.model.TestCategoriseSingleRequest;

@ActiveProfiles("tsetBased")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WSApplicationTest.class)
@WebAppConfiguration
@IntegrationTest({ "server.port=8085", "management.port=8085" })
public class TaxonomyControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(TaxonomyControllerTest.class);

    private static final String WS_URL = "http://localhost:8085/";

    private static final String WS_PATH_SEARCH = "taxonomy/search";

    private static final String WS_PATH_PUBLISH = "taxonomy/tset/publish";

    // private static final Logger logger =
    // LoggerFactory.getLogger(TestTaxonomyController.class);

    private static final String TEST_CATEGORY_CIAID = "C10039";

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

    @SuppressWarnings("rawtypes")
    @Test
    public final void testSearchIaView() {
	SearchIAViewRequest request = new SearchIAViewRequest();
	request.setCategoryQuery(IAViewRepositoryTest.QUERY_WITHOUT_WILDCARD);
	request.setLimit(10);
	PaginatedList iaViews = restTemplate.postForObject(WS_URL + WS_PATH_SEARCH, request, PaginatedList.class);
	assertThat(iaViews, is(notNullValue()));
	assertThat(iaViews.getResults(), is(notNullValue()));
	assertThat(iaViews.size(), is(not(equalTo(0))));
    }

    @Test
    public final void testPublicationWasSuccesful() throws InterruptedException {
	Category category = catRepo.findByCiaid(TEST_CATEGORY_CIAID);

	ResponseEntity<String> response = doPublishPostRequestOnWS(category);

	assertThat(response.getBody(), is(notNullValue()));
	assertThat(response.getBody(), containsString("OK"));

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
    public final void testTestCategoriseSingleDocumentWithMissingDescriptionAndDocRef() {
	HttpHeaders headers = new HttpHeaders();
	headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	headers.setContentType((MediaType.APPLICATION_JSON));

	TestCategoriseSingleRequest request = new TestCategoriseSingleRequest();
	request.setTitle("TRINITY Church of England School.");
	HttpEntity<TestCategoriseSingleRequest> requestEntity = new HttpEntity<TestCategoriseSingleRequest>(request,
		headers);

	ResponseEntity<TaxonomyErrorResponse> response = restTemplate.postForEntity(WS_URL
		+ WS_PATH_TEST_CATEGORISE_SINGLE, requestEntity, TaxonomyErrorResponse.class);

	assertThat(response.getBody(), is(notNullValue()));
	assertThat(response.getBody().getError(), equalTo(TaxonomyErrorType.INVALID_PARAMETER));

    }

    @SuppressWarnings("unchecked")
    @Test
    public final void testTestCategoriseSingleDocumentWithOnlyDescriptionAndDocRef() {
	TestCategoriseSingleRequest request = new TestCategoriseSingleRequest();
	request.setDescription("CHIEF OF STAFF, SUPREME ALLIED COMMAND: Operation \"Round-up\": operational organisation of RAF.");
	request.setDocReference("C508096");
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
	    Thread.sleep(200);
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

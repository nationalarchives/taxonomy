package uk.gov.nationalarchives.discovery.taxonomy.ws.controller;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetView;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.CategorisationResult;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.PaginatedList;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.exception.TaxonomyErrorType;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.exception.TaxonomyException;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.CategoriserService;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.IAViewService;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.TrainingSetService;
import uk.gov.nationalarchives.discovery.taxonomy.ws.model.PublishRequest;
import uk.gov.nationalarchives.discovery.taxonomy.ws.model.SearchIAViewRequest;
import uk.gov.nationalarchives.discovery.taxonomy.ws.model.TestCategoriseSingleRequest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@SuppressWarnings({ "rawtypes", "unchecked" })
@RestController
@EnableAutoConfiguration
@RequestMapping("/taxonomy")
public class TaxonomyController {

    private static final String STATUS_OK_JSON_RESPONSE = "{\"status\":\"OK\"}";

    private static final String APPLICATION_JSON = "application/json";
    private static final Logger logger = LoggerFactory.getLogger(TaxonomyController.class);

    @Autowired
    IAViewService iaViewService;

    @Autowired
    CategoriserService categoriser;

    @Autowired
    TrainingSetService trainingSetService;

    @RequestMapping(value = "/search", method = RequestMethod.POST, consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
    @ResponseBody
    PaginatedList<InformationAssetView> searchIAView(@RequestBody SearchIAViewRequest searchRequest) throws Exception {

	logger.info("/search > {}", searchRequest.toString());

	if (StringUtils.isEmpty(searchRequest.getCategoryQuery())) {
	    throw new TaxonomyException(TaxonomyErrorType.INVALID_CATEGORY_QUERY,
		    "categoryQuery should be provided and not empty");
	}
	if (searchRequest.getLimit() == null) {
	    searchRequest.setLimit(10);
	}
	if (searchRequest.getOffset() == null) {
	    searchRequest.setOffset(0);
	}

	PaginatedList<InformationAssetView> listOfIAViews = iaViewService.performSearch(
		searchRequest.getCategoryQuery(), searchRequest.getScore(), searchRequest.getLimit(),
		searchRequest.getOffset());

	logger.info("/search < {} IAViews returned, {} IAViews found", listOfIAViews.size(),
		listOfIAViews.getNumberOfResults());
	if (!CollectionUtils.isEmpty(listOfIAViews.getResults())) {
	    logger.info("/search < first element: {}", listOfIAViews.getResults().get(0).toString());
	}

	return listOfIAViews;
    }

    @RequestMapping(value = "/publish", method = RequestMethod.POST, consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
    @ResponseBody
    String publish(@RequestBody(required = true) PublishRequest publishRequest) {
	logger.info("/publish > {}", publishRequest.toString());

	trainingSetService.publishUpdateOnCategory(publishRequest.getCiaid());

	logger.info("/publish < {}", STATUS_OK_JSON_RESPONSE);
	return STATUS_OK_JSON_RESPONSE;
    }

    @RequestMapping(value = "/testCategoriseSingle", method = RequestMethod.POST, consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
    @ResponseBody
    List<CategorisationResult> testCategoriseSingle(@RequestBody TestCategoriseSingleRequest testCategoriseSingleRequest) {
	logger.info("/testCategoriseSingle > {}", testCategoriseSingleRequest.toString());
	if (StringUtils.isEmpty(testCategoriseSingleRequest.getDocReference())) {
	    throw new TaxonomyException(TaxonomyErrorType.INVALID_PARAMETER,
		    "DOCREFERENCE should be provided and not empty");
	}

	List<CategorisationResult> listOfCatRelevancies = categoriser.testCategoriseSingle(testCategoriseSingleRequest
		.getDocReference());

	logger.info("/testCategoriseSingle < {} categories", listOfCatRelevancies.size());

	if (!listOfCatRelevancies.isEmpty()) {
	    logger.info("/testCategoriseSingle < first element: {}", listOfCatRelevancies.get(0).toString());
	}
	return listOfCatRelevancies;
    }
}

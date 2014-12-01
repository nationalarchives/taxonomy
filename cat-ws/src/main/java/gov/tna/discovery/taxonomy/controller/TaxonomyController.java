package gov.tna.discovery.taxonomy.controller;

import gov.tna.discovery.taxonomy.domain.CategoryRelevancy;
import gov.tna.discovery.taxonomy.domain.PublishRequest;
import gov.tna.discovery.taxonomy.domain.SearchIAViewRequest;
import gov.tna.discovery.taxonomy.domain.TestCategoriseSingleRequest;
import gov.tna.discovery.taxonomy.repository.domain.lucene.InformationAssetView;
import gov.tna.discovery.taxonomy.service.TaxonomyWSService;
import gov.tna.discovery.taxonomy.service.exception.TaxonomyErrorType;
import gov.tna.discovery.taxonomy.service.exception.TaxonomyException;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
@RequestMapping("/taxonomy")
public class TaxonomyController {

    private static final String STATUS_OK_JSON_RESPONSE = "{\"status\":\"OK\"}";

    private static final String APPLICATION_JSON = "application/json";
    private static final Logger logger = LoggerFactory.getLogger(TaxonomyController.class);

    @Autowired
    private TaxonomyWSService service;

    @RequestMapping(value = "/search", method = RequestMethod.POST, consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
    @ResponseBody
    List<InformationAssetView> searchIAView(@RequestBody SearchIAViewRequest searchRequest) throws Exception {

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

	List<InformationAssetView> listOfIAViews = service.performSearch(searchRequest.getCategoryQuery(),
		searchRequest.getScore(), searchRequest.getLimit(), searchRequest.getOffset());

	logger.info("/search < {} IAViews", listOfIAViews.size());
	if (!listOfIAViews.isEmpty()) {
	    logger.info("/search < first element: {}", listOfIAViews.get(0).toString());
	}

	return listOfIAViews;
    }

    @RequestMapping(value = "/publish", method = RequestMethod.POST, consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
    @ResponseBody
    String publish(@RequestBody(required = true) PublishRequest publishRequest) {
	logger.info("/publish > {}", publishRequest.toString());

	service.publishUpdateOnCategory(publishRequest.getCiaid());

	logger.info("/publish < {}", STATUS_OK_JSON_RESPONSE);
	return STATUS_OK_JSON_RESPONSE;
    }

    @RequestMapping(value = "/testCategoriseSingle", method = RequestMethod.POST, consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
    @ResponseBody
    List<CategoryRelevancy> testCategoriseSingle(@RequestBody TestCategoriseSingleRequest testCategoriseSingleRequest) {
	logger.info("/testCategoriseSingle > {}", testCategoriseSingleRequest.toString());
	if (StringUtils.isEmpty(testCategoriseSingleRequest.getDescription())) {
	    throw new TaxonomyException(TaxonomyErrorType.INVALID_PARAMETER,
		    "DESCRIPTION should be provided and not emptyw");
	}

	List<CategoryRelevancy> listOfCatRelevancies = service.testCategoriseSingle(testCategoriseSingleRequest);

	logger.info("/testCategoriseSingle < {} categories", listOfCatRelevancies.size());

	if (!listOfCatRelevancies.isEmpty()) {
	    logger.info("/testCategoriseSingle < first element: {}", listOfCatRelevancies.get(0).toString());
	}
	return listOfCatRelevancies;
    }
}

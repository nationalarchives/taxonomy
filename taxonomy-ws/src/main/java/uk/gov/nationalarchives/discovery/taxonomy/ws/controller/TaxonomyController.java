/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.ws.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.exception.TaxonomyErrorType;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.exception.TaxonomyException;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetView;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.CategorisationResult;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.PaginatedList;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.CategoriserService;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.IAViewService;
import uk.gov.nationalarchives.discovery.taxonomy.ws.model.SearchIAViewRequest;
import uk.gov.nationalarchives.discovery.taxonomy.ws.model.TestCategoriseSingleRequest;

/**
 * WS for Taxonomy GUI:<br/>
 * provides methods to retrieve results from search queries and test
 * categorisation on documents
 * 
 * @author jcharlet
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
@RestController
@RequestMapping("/taxonomy")
public class TaxonomyController {

    private static final String APPLICATION_JSON = "application/json";
    private static final Logger logger = LoggerFactory.getLogger(TaxonomyController.class);

    @Autowired
    IAViewService iaViewService;

    @Autowired
    CategoriserService categoriser;

    /**
     * Endpoint: search for InformationAssetViews in lucene index using a
     * category query and other parameters
     * 
     * @param searchRequest
     * @return paginated list of found information asset views
     * @throws Exception
     */
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

    /**
     * Endpoint: test the categorisation of an information asset view
     * 
     * @param testCategoriseSingleRequest
     * @return the list of categorisation results
     */
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

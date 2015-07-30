/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.ws.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import uk.gov.nationalarchives.discovery.taxonomy.common.service.TrainingSetService;
import uk.gov.nationalarchives.discovery.taxonomy.ws.model.PublishRequest;

/**
 * WS for Taxonomy GUI dedicated to training set:<br/>
 * provides methods to update the training set when taxonomists are satisfied
 * with a category
 * 
 * @author jcharlet
 *
 */
@RestController
@RequestMapping("/taxonomy/tset")
@ConditionalOnProperty(prefix = "lucene.categoriser.", value = "useTSetBasedCategoriser")
public class TaxonomyTrainingSetController {

    private static final String STATUS_OK_JSON_RESPONSE = "{\"status\":\"OK\"}";

    private static final String APPLICATION_JSON = "application/json";
    private static final Logger logger = LoggerFactory.getLogger(TaxonomyTrainingSetController.class);

    @Autowired
    TrainingSetService trainingSetService;

    /**
     * Endpoint: Publish the modifications on a category
     * 
     * @see TrainingSetService#publishUpdateOnCategory(String)
     * @param publishRequest
     * @return "OK"
     */
    @RequestMapping(value = "/publish", method = RequestMethod.POST, consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
    @ResponseBody
    String publish(@RequestBody(required = true) PublishRequest publishRequest) {
	logger.info("/publish > {}", publishRequest.toString());

	trainingSetService.publishUpdateOnCategory(publishRequest.getCiaid());

	logger.info("/publish < {}", STATUS_OK_JSON_RESPONSE);
	return STATUS_OK_JSON_RESPONSE;
    }
}

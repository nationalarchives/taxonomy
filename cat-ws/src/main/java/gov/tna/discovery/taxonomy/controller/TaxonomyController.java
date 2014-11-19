package gov.tna.discovery.taxonomy.controller;

import gov.tna.discovery.taxonomy.domain.PublishRequest;
import gov.tna.discovery.taxonomy.domain.SearchIAViewRequest;
import gov.tna.discovery.taxonomy.repository.domain.lucene.InformationAssetView;
import gov.tna.discovery.taxonomy.service.TaxonomyWSService;
import gov.tna.discovery.taxonomy.service.exception.TaxonomyErrorType;
import gov.tna.discovery.taxonomy.service.exception.TaxonomyException;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
@RequestMapping("/taxonomy")
public class TaxonomyController {

    private static final String STATUS_OK_JSON_RESPONSE = "{\"status\":\"OK\"}";

    private static final String APPLICATION_JSON = "application/json";
    // private static final Logger logger =
    // LoggerFactory.getLogger(TaxonomyController.class);

    @Autowired
    private TaxonomyWSService service;

    @RequestMapping(value = "/search", method = RequestMethod.POST, consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
    @ResponseBody
    List<InformationAssetView> searchIAView(@RequestBody SearchIAViewRequest searchRequest) throws Exception {
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
	return service.performSearch(searchRequest.getCategoryQuery(), searchRequest.getScore(),
		searchRequest.getLimit(), searchRequest.getOffset());
    }

    @RequestMapping(value = "/publish", method = RequestMethod.POST, consumes = APPLICATION_JSON, produces = APPLICATION_JSON)
    @ResponseBody
    String publish(@RequestBody PublishRequest publishRequest) {
	service.publishUpdateOnCategory(publishRequest.getCiaid(), null);
	return STATUS_OK_JSON_RESPONSE;
    }

}

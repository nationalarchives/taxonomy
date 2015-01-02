package gov.tna.discovery.taxonomy.common.service.impl;

import gov.tna.discovery.taxonomy.common.service.LegacySystemService;
import gov.tna.discovery.taxonomy.common.service.domain.legacy.LegacySearchResponse;
import gov.tna.discovery.taxonomy.common.service.domain.legacy.SearchResultList;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

/**
 * Service dedicated to querying the legacy Autonomy system<br/>
 * used to evaluate the current categorisation system
 * 
 * @author jcharlet
 *
 */
@Service
public class LegacySystemServiceImpl implements LegacySystemService {
    private static final Logger logger = LoggerFactory.getLogger(EvaluationServiceImpl.class);

    private String legacySystemHostUrl = "http://test.legacy.discovery.nationalarchives.gov.uk/DiscoveryAPI/json/";
    private String legacySystemSearchExactUrl = "search/{page}/exact={catdocref}";
    private String legacySystemSearchQueryUrl = "search/{page}/query={queryValue}";
    private int proxyPort = 8080;
    private String proxyUrl = "***REMOVED***.***REMOVED***";

    /*
     * (non-Javadoc)
     * 
     * @see gov.tna.discovery.taxonomy.common.service.impl.LegacySystemService#
     * getLegacyCategoriesForCatDocRef(java.lang.String)
     */
    @Override
    public String[] getLegacyCategoriesForCatDocRef(String pCatdocref) {
	logger.debug(".getLegacyCategoriesForCatDocRef> processing catDocRef: {}", pCatdocref);

	String catdocref = pCatdocref.replaceAll("\\<.*?>", " ");
	catdocref = catdocref.replace("/", " ");
	if (StringUtils.isEmpty(StringUtils.trimAllWhitespace(catdocref))) {
	    logger.error(".getLegacyCategoriesForCatDocRef : once cleaned, the parameter is an empty string",
		    pCatdocref);
	    return null;
	}

	ResponseEntity<LegacySearchResponse> entityResponse = submitSearchRequest(legacySystemSearchExactUrl,
		catdocref, 1);

	if (isLegacySystemResponseValid(entityResponse)) {
	    return null;
	}

	return getLegacyCategoriesFromResponse(entityResponse);
    }

    private ResponseEntity<LegacySearchResponse> submitSearchRequest(String url, String parameterValue, int page) {
	SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
	requestFactory.setProxy(new Proxy(Type.HTTP, new InetSocketAddress(proxyUrl, proxyPort)));
	try {
	    RestTemplate restTemplate = new RestTemplate(requestFactory);
	    ResponseEntity<LegacySearchResponse> entityResponse = restTemplate.getForEntity(legacySystemHostUrl + url,
		    LegacySearchResponse.class, page, parameterValue);
	    return entityResponse;
	} catch (Exception e) {
	    logger.error(".getLegacyCategoriesForCatDocRef : exception occured", e);
	    return null;
	}
    }

    private String[] getLegacyCategoriesFromResponse(ResponseEntity<LegacySearchResponse> entityResponse) {
	LegacySearchResponse searchResponse = entityResponse.getBody();
	List<String> subjects = searchResponse.getSearchResult().getSearchResultList().get(0).getSubjects();
	String[] legacyCategories = new String[subjects.size()];
	for (int i = 0; i < subjects.size(); i++) {
	    legacyCategories[i] = subjects.get(i).substring(7);

	}
	return legacyCategories;
    }

    private boolean isLegacySystemResponseValid(ResponseEntity<LegacySearchResponse> entityResponse) {
	if (!HttpStatus.OK.equals(entityResponse.getStatusCode())) {
	    logger.error(".getLegacyCategoriesForCatDocRef : response from legacy system != 200: {}",
		    entityResponse.getStatusCode());
	    return true;
	}
	LegacySearchResponse searchResponse = entityResponse.getBody();
	if ((searchResponse.getSearchResult().getTotalResults() == null)
		|| searchResponse.getSearchResult().getTotalResults() == 0) {
	    logger.info(".getLegacyCategoriesForCatDocRef : no element found");
	    return true;
	}
	return false;
    }

    @Override
    public Map<String, String[]> findLegacyDocumentsByCategory(String category, Integer page) {
	ResponseEntity<LegacySearchResponse> entityResponse = submitSearchRequest(legacySystemSearchQueryUrl, category,
		page);

	if (isLegacySystemResponseValid(entityResponse)) {
	    return null;
	}

	return getMapOfDocumentIaidsWithCategoriesFromResponse(entityResponse);
    }

    private Map<String, String[]> getMapOfDocumentIaidsWithCategoriesFromResponse(
	    ResponseEntity<LegacySearchResponse> entityResponse) {
	Map<String, String[]> mapOfDocumentIaidsWithCategories = new HashMap<String, String[]>();

	LegacySearchResponse searchResponse = entityResponse.getBody();
	for (SearchResultList searchResultList : searchResponse.getSearchResult().getSearchResultList()) {
	    String[] categories = getFormattedSubjects(searchResultList.getSubjects());
	    mapOfDocumentIaidsWithCategories.put(searchResultList.getIAID(), categories);
	}

	return mapOfDocumentIaidsWithCategories;
    }

    private String[] getFormattedSubjects(List<String> subjects) {
	List<String> categories = new ArrayList<String>();
	for (String subject : subjects) {
	    categories.add(subject.substring(7));
	}
	return categories.toArray(new String[0]);
    }
}

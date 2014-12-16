package gov.tna.discovery.taxonomy.common.service.impl;

import gov.tna.discovery.taxonomy.common.service.LegacySystemService;
import gov.tna.discovery.taxonomy.common.service.domain.legacy.LegacySearchResponse;
import gov.tna.discovery.taxonomy.common.service.exception.TaxonomyErrorType;
import gov.tna.discovery.taxonomy.common.service.exception.TaxonomyException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.SocketAddress;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpInetSocketAddress;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
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

    private String legacySystemUrl = "http://test.legacy.discovery.nationalarchives.gov.uk/DiscoveryAPI/json/search/1/exact={catdocref}";
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

	SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
	requestFactory.setProxy(new Proxy(Type.HTTP, new InetSocketAddress(proxyUrl, proxyPort)));

	ResponseEntity<LegacySearchResponse> entityResponse;
	try {
	    RestTemplate restTemplate = new RestTemplate(requestFactory);
	    entityResponse = restTemplate.getForEntity(legacySystemUrl, LegacySearchResponse.class, catdocref);
	} catch (Exception e) {
	    logger.error(".getLegacyCategoriesForCatDocRef : exception occured", e);
	    return null;
	}

	if (hasErrors(entityResponse)) {
	    return null;
	}

	return getLegacyCategoriesFromResponse(entityResponse);
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

    private boolean hasErrors(ResponseEntity<LegacySearchResponse> entityResponse) {
	if (!HttpStatus.OK.equals(entityResponse.getStatusCode())) {
	    logger.error(".getLegacyCategoriesForCatDocRef : response from legacy system != 200: {}",
		    entityResponse.getStatusCode());
	    return true;
	}
	LegacySearchResponse searchResponse = entityResponse.getBody();
	if ((searchResponse.getSearchResult().getTotalResults() == null)
		|| searchResponse.getSearchResult().getTotalResults() == 0) {
	    logger.error(".getLegacyCategoriesForCatDocRef : no element found");
	    return true;
	}
	return false;
    }

}

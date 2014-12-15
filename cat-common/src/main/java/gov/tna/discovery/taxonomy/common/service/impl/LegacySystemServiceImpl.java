package gov.tna.discovery.taxonomy.common.service.impl;

import gov.tna.discovery.taxonomy.common.service.LegacySystemService;
import gov.tna.discovery.taxonomy.common.service.exception.TaxonomyErrorType;
import gov.tna.discovery.taxonomy.common.service.exception.TaxonomyException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.Arrays;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    private String legacySystemUrl = "http://test.legacy.discovery.nationalarchives.gov.uk/SearchUI/s/res?_q=";
    private int proxyPort = 8080;
    private String proxyUrl = "***REMOVED***.***REMOVED***";
    private CharSequence categoriesLineToFind = "<span>Subjects:</span>";
    private String elementBeginIndexString = "<span class=\"itemContent\">";
    private String elementEndIndexString = "</span>";
    private CharSequence referenceLineToFind = "<span>Reference:</span>";

    /* (non-Javadoc)
     * @see gov.tna.discovery.taxonomy.common.service.impl.LegacySystemService#getLegacyCategoriesForCatDocRef(java.lang.String)
     */
    @Override
    public String[] getLegacyCategoriesForCatDocRef(String catdocref) {
	String[] legacyCategories = null;
	CloseableHttpClient httpclient = HttpClients.createDefault();
	try {
	    String url = legacySystemUrl + URLEncoder.encode(catdocref, "UTF-8");
	    logger.debug(".getLegacyCategoriesForCatDocRef: url: {}", url);

	    HttpGet target = new HttpGet(url);
	    HttpHost proxy = new HttpHost(proxyUrl, proxyPort, "http");

	    RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
	    target.setConfig(config);

	    logger.debug("Executing request " + target.getRequestLine() + " to " + target + " via " + proxy);

	    CloseableHttpResponse response = httpclient.execute(target);
	    try {
		BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		String inputLine;

		while ((inputLine = in.readLine()) != null) {
		    if (inputLine.contains(referenceLineToFind)) {
			int referenceBeginIndex = inputLine.lastIndexOf(elementBeginIndexString)
				+ elementBeginIndexString.length();
			int referenceEndIndex = inputLine.indexOf(elementEndIndexString, referenceBeginIndex);
			String reference = inputLine.substring(referenceBeginIndex, referenceEndIndex);
			logger.debug("for doc {}, found reference: {}", catdocref, reference);
		    }
		    if (inputLine.contains(categoriesLineToFind)) {
			legacyCategories = getCategoriesFromHtmlInputLine(inputLine);
			logger.debug("for doc {}, found legacy cats: {}", catdocref, Arrays.toString(legacyCategories));
			break;
		    }
		}
	    } finally {
		response.close();
	    }
	} catch (Exception e) {
	    logger.error(".getLegacyCategoriesForCatDocRef exception thrown", e);
	    throw new TaxonomyException(TaxonomyErrorType.HTTPCLIENT_ERROR, e);
	} finally {
	    try {
		httpclient.close();
	    } catch (IOException e) {
		throw new TaxonomyException(TaxonomyErrorType.HTTPCLIENT_ERROR, e);
	    }
	}
	return legacyCategories;
    }

    private String[] getCategoriesFromHtmlInputLine(String inputLine) {
	String[] legacyCategories;
	int categoriesBeginIndex = inputLine.lastIndexOf(elementBeginIndexString) + elementBeginIndexString.length();
	int categoriesEndIndex = inputLine.indexOf(elementEndIndexString, categoriesBeginIndex);
	String categoriesString = inputLine.substring(categoriesBeginIndex, categoriesEndIndex);
	legacyCategories = StringUtils.delimitedListToStringArray(categoriesString, " |  ");
	return legacyCategories;
    }

}

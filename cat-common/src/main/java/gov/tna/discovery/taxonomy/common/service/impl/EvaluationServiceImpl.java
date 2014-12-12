package gov.tna.discovery.taxonomy.common.service.impl;

import gov.tna.discovery.taxonomy.common.repository.domain.lucene.InformationAssetView;
import gov.tna.discovery.taxonomy.common.repository.domain.mongo.Category;
import gov.tna.discovery.taxonomy.common.repository.domain.mongo.TestDocument;
import gov.tna.discovery.taxonomy.common.repository.lucene.IAViewRepository;
import gov.tna.discovery.taxonomy.common.repository.mongo.CategoryRepository;
import gov.tna.discovery.taxonomy.common.repository.mongo.TestDocumentRepository;
import gov.tna.discovery.taxonomy.common.service.domain.PaginatedList;
import gov.tna.discovery.taxonomy.common.service.exception.TaxonomyErrorType;
import gov.tna.discovery.taxonomy.common.service.exception.TaxonomyException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.Arrays;

import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EvaluationServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(EvaluationServiceImpl.class);
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private IAViewRepository iaviewRepository;

    @Autowired
    private TestDocumentRepository testDocumentRepository;

    private Integer minNbOfElementsPerCat = 10;
    private String legacySystemUrl = "http://test.legacy.discovery.nationalarchives.gov.uk/SearchUI/s/res?_q=";
    private int proxyPort = 8080;
    private String proxyUrl = "***REMOVED***.***REMOVED***";
    private CharSequence categoriesLineToFind = "Subjects:";

    public void createTestDataset() {
	for (Category category : categoryRepository.findAll()) {
	    Integer nbOfMatchedElementsWithLegacySystem = 0;
	    Integer offset = 0;
	    while (nbOfMatchedElementsWithLegacySystem < minNbOfElementsPerCat) {
		PaginatedList<InformationAssetView> iaviews = iaviewRepository.performSearch(category.getQry(), null,
			10, offset);
		for (InformationAssetView iaview : iaviews.getResults()) {
		    String[] legacyCategories = getLegacyCategoriesForCatDocRef(iaview.getCATDOCREF());
		    if (legacyCategories != null && Arrays.asList(legacyCategories).contains(category.getTtl())) {
			TestDocument testDocument = new TestDocument();
			testDocument = getTestDocumentFromIAView(iaview);
			testDocument.setLegacyCategories(legacyCategories);
			testDocumentRepository.save(testDocument);
			nbOfMatchedElementsWithLegacySystem++;
		    }
		}
		if (nbOfMatchedElementsWithLegacySystem == 0) {
		    logger.warn(".createTestDataset: giving up, no results found for category: {}", category.getTtl());
		    break;
		}
		offset += 10;
		if (offset >= iaviews.getNumberOfResults()) {
		    continue;
		}
	    }
	}
    }

    private TestDocument getTestDocumentFromIAView(InformationAssetView iaView) {
	TestDocument testDocument = new TestDocument();
	testDocument.setDescription(iaView.getDESCRIPTION());
	testDocument.setContextDescription(iaView.getCONTEXTDESCRIPTION());
	testDocument.setTitle(iaView.getTITLE());
	testDocument.setDocReference(iaView.getDOCREFERENCE());
	testDocument.setCatDocRef(iaView.getCATDOCREF());
	testDocument.setCorpBodys(iaView.getCORPBODYS());
	testDocument.setPersonFullName(iaView.getPERSON_FULLNAME());
	testDocument.setPlaceName(iaView.getPLACE_NAME());
	testDocument.setSubjects(iaView.getSUBJECTS());
	return testDocument;
    }

    private String[] getLegacyCategoriesForCatDocRef(String catdocref) {
	String[] legacyCategories = null;
	CloseableHttpClient httpclient = HttpClients.createDefault();
	try {
	    String url = legacySystemUrl + URLEncoder.encode(catdocref, "UTF-8");
	    logger.debug(".getLegacyCategoriesForCatDocRef: url: {}", url);

	    HttpHost target = new HttpHost(url, 80, "https");
	    HttpHost proxy = new HttpHost(proxyUrl, proxyPort, "http");

	    RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
	    HttpGet request = new HttpGet("/");
	    request.setConfig(config);

	    logger.debug("Executing request " + request.getRequestLine() + " to " + target + " via " + proxy);

	    CloseableHttpResponse response = httpclient.execute(target, request);
	    try {
		BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		String inputLine;

		while ((inputLine = in.readLine()) != null) {
		    System.out.println(inputLine);
		    if (inputLine.contains(categoriesLineToFind)) {
			int beginIndex = inputLine.lastIndexOf("<span class=\"itemContent\">");
			int endIndex = inputLine.indexOf("</span>", beginIndex);
			String categoriesString = inputLine.substring(beginIndex, endIndex);
			legacyCategories = categoriesString.split(" |  ");
			logger.debug("for doc {}, found legacy cats: {}", catdocref, legacyCategories.toString());
			break;
		    }
		}
	    } finally {
		response.close();
	    }
	} catch (Exception e) {
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

    public void evaluateCategorisation() {

    }

    public String getEvaluationReport() {
	return null;
    }

}

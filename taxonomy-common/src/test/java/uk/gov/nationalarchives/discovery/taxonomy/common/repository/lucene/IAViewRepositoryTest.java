package uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.gov.nationalarchives.discovery.taxonomy.common.config.LuceneConfigurationTest;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetView;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetViewFields;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.PaginatedList;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.tools.LuceneHelperTools;

@ActiveProfiles("tsetBased")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = LuceneConfigurationTest.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IAViewRepositoryTest {

    @Value("${lucene.index.defaultTaxonomyField}")
    private String defaultTaxonomyField;

    public static final String QUERY_WITHOUT_WILDCARD = "\"air force\"";
    private static final String TERM_VALUE = "attacks";

    public static final Logger logger = LoggerFactory.getLogger(IAViewRepositoryTest.class);

    @Autowired
    private IAViewRepository iaViewRepository;

    @Autowired
    private SearcherManager iaviewSearcherManager;

    @Autowired
    private Directory iaViewDirectory;

    @Autowired
    private Analyzer iaViewSearchAnalyser;

    @Autowired
    private LuceneHelperTools luceneHelperTools;

    @Value("${lucene.index.version}")
    private String luceneVersion;

    private static final String QUERY_WITH_LEADING_WILDCARD = "*quarters";

    @Test
    public void testPerformSearchWithLeadingWildcard() {
	PaginatedList<InformationAssetView> results = iaViewRepository.performSearch(QUERY_WITH_LEADING_WILDCARD, null,
		100, 0);
	assertThat(results, is(notNullValue()));
	assertThat(results.getResults(), is(not(empty())));
	assertThat(results.size(), is(equalTo(100)));
	logger.debug(".testPerformSearchWithLeadingWildcard: Found {} results", results.size());
    }

    @Test
    public void testPerformSearchWithQueryWithoutWildCard() {
	PaginatedList<InformationAssetView> results = iaViewRepository.performSearch(QUERY_WITHOUT_WILDCARD, null, 100,
		0);
	assertThat(results, is(notNullValue()));
	assertThat(results.getResults(), is(notNullValue()));
	assertThat(results.getResults(), is(not(empty())));
	logger.debug(".testPerformSearchWithQueryWithoutWildCard: Found {} results", results.size());
    }

    @Test
    public void testPerformSearchWithQueryWithMinimumScoreWithLimit() {
	PaginatedList<InformationAssetView> results = iaViewRepository.performSearch(TERM_VALUE, 0.62, 2, 0);
	assertThat(results, is(notNullValue()));
	assertThat(results.getResults(), is(notNullValue()));
	assertThat(results.getResults(), is(not(empty())));
	assertThat(results.size(), is(equalTo(2)));
	assertThat(results.getNumberOfResults(), is(equalTo(26)));
	logger.debug(".testPerformSearchWithQueryWithMinimumScore: Returned {} results and found {} results in total",
		results.size(), results.getNumberOfResults());
    }

    @Test
    public void testPerformSearchWithQueryWithMinimumScoreWithHighLimit() {
	PaginatedList<InformationAssetView> results = iaViewRepository.performSearch(QUERY_WITHOUT_WILDCARD, 0.62, 100,
		0);
	assertThat(results, is(notNullValue()));
	assertThat(results.getResults(), is(notNullValue()));
	assertThat(results.getResults(), is(not(empty())));
	assertThat(results.size(), is(equalTo(30)));
	assertThat(results.getNumberOfResults(), is(equalTo(30)));
	logger.debug(".testPerformSearchWithQueryWithMinimumScore: Returned {} results and found {} results in total",
		results.size(), results.getNumberOfResults());
    }

    @Test
    public void testGetNbOfElementsAboveScore() throws IOException {
	IndexSearcher isearcher = iaviewSearcherManager.acquire();

	Query query = new WildcardQuery(new Term(defaultTaxonomyField, TERM_VALUE));
	Integer nbOfElementsAboveScore = iaViewRepository.getNbOfElementsAboveScore(0.62, isearcher, query);
	assertThat(nbOfElementsAboveScore, is(equalTo(28)));
    }

    // @Test
    // @Ignore("to work on filters")
    public void testPerformSearchWithQueryWithFilter() {

	List<Filter> filters = new ArrayList<Filter>();

	String catDocRef = "BT 351/1/102117";
	String docRef = "D8068594";

	filters.add(null);
	filters.add(new QueryWrapperFilter(new TermQuery(new Term(InformationAssetViewFields.DOCREFERENCE.toString(),
		docRef.toLowerCase()))));
	filters.add(new QueryWrapperFilter(new TermQuery(new Term(InformationAssetViewFields.DOCREFERENCE.toString(),
		docRef))));
	filters.add(new QueryWrapperFilter(luceneHelperTools.buildSearchQuery("\"" + catDocRef + "\"")));
	filters.add(new QueryWrapperFilter(luceneHelperTools.buildSearchQuery("CATDOCREF:\"" + catDocRef + "\"")));
	filters.add(null);

	TopDocs results = null;
	for (Filter filter2 : filters) {
	    results = iaViewRepository.performSearchWithoutAnyPostProcessing("Daniel", filter2, 0d, 1, 0);
	    logger.info("testing filter: {}, found {} results", String.valueOf(filter2), results.scoreDocs.length);
	}
	assertThat(results, is(notNullValue()));
	assertThat(results.totalHits, is(equalTo(1)));
	assertThat(results.scoreDocs.length, is(equalTo(1)));
	logger.debug(".testPerformSearchWithQueryWithFilter: Returned {} results and found {} results in total",
		results.scoreDocs.length, results.totalHits);
    }

}

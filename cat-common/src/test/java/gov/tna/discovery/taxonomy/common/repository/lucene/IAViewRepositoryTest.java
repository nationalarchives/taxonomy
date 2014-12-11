package gov.tna.discovery.taxonomy.common.repository.lucene;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import gov.tna.discovery.taxonomy.common.config.LuceneConfigurationTest;
import gov.tna.discovery.taxonomy.common.repository.domain.lucene.InformationAssetView;
import gov.tna.discovery.taxonomy.common.service.domain.PaginatedList;

import java.io.IOException;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.WildcardQuery;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.expression.spel.ast.Indexer;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = LuceneConfigurationTest.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IAViewRepositoryTest {

    private static final String QUERY_WITHOUT_WILDCARD = "\"church\"";

    public static final Logger logger = LoggerFactory.getLogger(Indexer.class);

    @Autowired
    private IAViewRepository iaViewRepository;

    @Autowired
    private SearcherManager iaviewSearcherManager;

    private static final String QUERY_WITH_LEADING_WILDCARD = "*Church";

    @Test
    public void testPerformSearchWithLeadingWildcard() {
	PaginatedList<InformationAssetView> results = iaViewRepository.performSearch(QUERY_WITH_LEADING_WILDCARD, null,
		100, 0);
	assertThat(results, is(notNullValue()));
	assertThat(results.getResults(), is(not(empty())));
	assertThat(results.size(), is(equalTo(4)));
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
    public void testPerformSearchWithQueryWithMinimumScore() {
	PaginatedList<InformationAssetView> results = iaViewRepository.performSearch(QUERY_WITHOUT_WILDCARD, 0.005, 2,
		0);
	assertThat(results, is(notNullValue()));
	assertThat(results.getResults(), is(notNullValue()));
	assertThat(results.getResults(), is(not(empty())));
	assertThat(results.size(), is(equalTo(2)));
	assertThat(results.getNumberOfResults(), is(equalTo(3)));
	logger.debug(".testPerformSearchWithQueryWithMinimumScore: Returned {} results and found {} results in total",
		results.size(), results.getNumberOfResults());
    }

    @Test
    public void testGetNbOfElementsAboveScore() throws IOException {
	IndexSearcher isearcher = iaviewSearcherManager.acquire();

	Query query = new WildcardQuery(new Term("DESCRIPTION", "*record*"));
	Integer nbOfElementsAboveScore = iaViewRepository.getNbOfElementsAboveScore(0.001, isearcher, query);
	assertThat(nbOfElementsAboveScore, is(equalTo(101)));

    }
}

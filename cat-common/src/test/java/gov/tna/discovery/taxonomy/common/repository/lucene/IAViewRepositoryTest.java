package gov.tna.discovery.taxonomy.common.repository.lucene;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import gov.tna.discovery.taxonomy.common.config.LuceneConfigurationTest;
import gov.tna.discovery.taxonomy.common.mapper.LuceneTaxonomyMapper;
import gov.tna.discovery.taxonomy.common.repository.domain.lucene.InformationAssetView;
import gov.tna.discovery.taxonomy.common.repository.domain.lucene.InformationAssetViewFields;
import gov.tna.discovery.taxonomy.common.service.domain.PaginatedList;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.complexPhrase.ComplexPhraseQueryParser;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import org.junit.Assert;
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

@ActiveProfiles("tsetBased")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = LuceneConfigurationTest.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class IAViewRepositoryTest {

    public static final String QUERY_WITHOUT_WILDCARD = "\"Daniel\"";
    private static final String TERM_VALUE = "Daniel";

    public static final Logger logger = LoggerFactory.getLogger(IAViewRepositoryTest.class);

    @Autowired
    private IAViewRepository iaViewRepository;

    @Autowired
    private SearcherManager iaviewSearcherManager;

    @Autowired
    private Directory iaViewDirectory;

    @Autowired
    private Analyzer iaViewSearchAnalyser;

    @Value("${lucene.index.version}")
    private String luceneVersion;

    @Value("${lucene.index.fieldsToSearch}")
    private String fieldsToAnalyse;

    private static final String QUERY_WITH_LEADING_WILDCARD = "*stone";

    @Test
    public void testPerformSearchWithLeadingWildcard() {
	PaginatedList<InformationAssetView> results = iaViewRepository.performSearch(QUERY_WITH_LEADING_WILDCARD, null,
		100, 0);
	assertThat(results, is(notNullValue()));
	assertThat(results.getResults(), is(not(empty())));
	assertThat(results.size(), is(equalTo(3)));
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
	PaginatedList<InformationAssetView> results = iaViewRepository.performSearch("Daniel", 0.005, 2, 0);
	assertThat(results, is(notNullValue()));
	assertThat(results.getResults(), is(notNullValue()));
	assertThat(results.getResults(), is(not(empty())));
	assertThat(results.size(), is(equalTo(2)));
	assertThat(results.getNumberOfResults(), is(equalTo(6)));
	logger.debug(".testPerformSearchWithQueryWithMinimumScore: Returned {} results and found {} results in total",
		results.size(), results.getNumberOfResults());
    }

    @Test
    public void testPerformSearchWithQueryWithMinimumScoreWithHighLimit() {
	PaginatedList<InformationAssetView> results = iaViewRepository.performSearch(QUERY_WITHOUT_WILDCARD, 1.29, 100,
		0);
	assertThat(results, is(notNullValue()));
	assertThat(results.getResults(), is(notNullValue()));
	assertThat(results.getResults(), is(not(empty())));
	assertThat(results.size(), is(equalTo(1)));
	assertThat(results.getNumberOfResults(), is(equalTo(1)));
	logger.debug(".testPerformSearchWithQueryWithMinimumScore: Returned {} results and found {} results in total",
		results.size(), results.getNumberOfResults());
    }

    @Test
    public void testGetNbOfElementsAboveScore() throws IOException {
	IndexSearcher isearcher = iaviewSearcherManager.acquire();

	Query query = new WildcardQuery(new Term(InformationAssetViewFields.texttax.toString(), TERM_VALUE));
	Integer nbOfElementsAboveScore = iaViewRepository.getNbOfElementsAboveScore(0.005, isearcher, query);
	assertThat(nbOfElementsAboveScore, is(equalTo(6)));
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
	filters.add(new QueryWrapperFilter(iaViewRepository.buildSearchQuery("\"" + catDocRef + "\"")));
	filters.add(new QueryWrapperFilter(iaViewRepository.buildSearchQuery("CATDOCREF:\"" + catDocRef + "\"")));
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

    // @Test
    // @Ignore("to work on punctuation")
    public void testPerformSearchWithPunctuation() throws IOException {
	String[] queryStrings = { "\"ew\"", "\"1945-1979\"", "\"war office:\"", "\"users' national council\"",
		"\"(pounc)\"", "\"middle east forces; military headquarters papers, second world war.\"",
		"\"not closed on its disbandment but continued\"" };
	for (String queryString : Arrays.asList(queryStrings)) {
	    logger.info("WORKING ON {}", queryString);
	    PaginatedList<InformationAssetView> results = iaViewRepository.performSearch(queryString, 0d, 100, 0);

	    if (results.getNumberOfResults() != 0) {
		logger.info("found {} result", results.getNumberOfResults());
	    } else {
		logger.error("found {} result", results.getNumberOfResults());
	    }
	    // assertThat(results, is(notNullValue()));
	    // assertThat(results.getResults(), is(notNullValue()));
	    // assertThat(results.getResults(), is(not(empty())));
	    // assertThat(results.getNumberOfResults(), is(equalTo(1)));
	}
	Assert.fail();

    }

    // @Test
    // @Ignore("to work on punctuation")
    public void testPerformSearchWithPunctuationWithFakeIAView() throws IOException {
	IndexWriter writer = null;
	try {
	    String[] queryStrings = { "\"British National: B.B.C WO100.\"", "\"B.B.C\"", "\"WO100.\"",
		    "\"British National:\"", "B.B.C" };
	    writer = updateIAViewDirectoryWithOnePunctuatedDocument(this.iaViewSearchAnalyser);
	    for (String queryString : Arrays.asList(queryStrings)) {
		logger.info(queryString);
		PaginatedList<InformationAssetView> results = iaViewRepository.performSearch(queryString, 0d, 100, 0);

		if (results.getNumberOfResults() == 1) {
		    logger.info("found {} result", results.getNumberOfResults());
		} else {
		    logger.error("found {} result", results.getNumberOfResults());
		}
		assertThat(results, is(notNullValue()));
		assertThat(results.getResults(), is(notNullValue()));
		assertThat(results.getResults(), is(not(empty())));
		assertThat(results.getNumberOfResults(), is(equalTo(1)));
	    }
	    Assert.fail();

	} finally {
	    LuceneHelperTools.closeIndexWriterQuietly(writer);
	}
    }

    // @Test
    // @Ignore("to work on punctuation")
    public void ensureThatPunctuationIsIndexed() throws IOException {
	IndexWriter writer = null;
	try {
	    TokenStream tokenStream = this.iaViewSearchAnalyser.tokenStream("TITLE", new StringReader(
		    "B.B.C: Labour requirements for the housing programme."));
	    OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
	    CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);

	    tokenStream.reset();
	    tokenStream.incrementToken();
	    String term = charTermAttribute.toString();
	    logger.info(term);
	    assertThat(term, is(notNullValue()));
	    assertThat(term, is(equalTo("B.B.C:")));

	} finally {
	    LuceneHelperTools.closeIndexWriterQuietly(writer);
	}
    }

    // @Test
    // @Ignore("to work on punctuation")
    public void testPerformSearchWithHandMadeParser() throws IOException, ParseException {

	String[] queryStrings = { "\"B.B.C:\"", "BBC", "\"B.B.C\"", "B.B.C" };

	IndexWriter writer = null;
	SearcherManager iaviewSearcherManager = null;
	IndexSearcher isearcher = null;
	try {
	    Analyzer analyzer = new WhitespaceAnalyzer(Version.valueOf(luceneVersion));
	    writer = updateIAViewDirectoryWithOnePunctuatedDocument(analyzer);

	    iaviewSearcherManager = new SearcherManager(writer, true, null);
	    isearcher = iaviewSearcherManager.acquire();

	    QueryParser multiFieldQueryParser = new MultiFieldQueryParser(Version.valueOf(luceneVersion),
		    fieldsToAnalyse.split(","), analyzer);
	    multiFieldQueryParser.setAllowLeadingWildcard(true);

	    QueryParser simpleQueryParser = new QueryParser(Version.valueOf(luceneVersion), "TITLE", analyzer);

	    QueryParser complexPhraseQueryParser = new ComplexPhraseQueryParser(Version.valueOf(luceneVersion),
		    "TITLE", analyzer);

	    List<QueryParser> queryParsers = Arrays.asList(multiFieldQueryParser, simpleQueryParser,
		    complexPhraseQueryParser);

	    for (String queryString : Arrays.asList(queryStrings)) {
		try {
		    logger.info("WORKING on query {}", queryString);
		    for (QueryParser queryParser : queryParsers) {
			Query query = queryParser.parse(queryString);
			TopDocs topDocs = isearcher.search(query, 1);

			if (topDocs.totalHits == 1) {
			    logger.info("found {} result for parser {}", topDocs.totalHits, queryParser.getClass());
			} else {
			    logger.error("found {} result for parser {}", topDocs.totalHits, queryParser.getClass());
			}
		    }
		} catch (Exception e) {
		    logger.error("error occured", e.getMessage());
		}
	    }

	    Assert.fail();

	} finally {
	    LuceneHelperTools.closeIndexWriterQuietly(writer);
	    LuceneHelperTools.releaseSearcherManagerQuietly(iaviewSearcherManager, isearcher);
	}
    }

    private IndexWriter updateIAViewDirectoryWithOnePunctuatedDocument(Analyzer analyser) throws IOException {
	IndexWriter writer;
	writer = new IndexWriter(iaViewDirectory, new IndexWriterConfig(Version.valueOf(luceneVersion), analyser));
	writer.deleteAll();
	InformationAssetView iaView = new InformationAssetView();
	iaView.setCATDOCREF("HLG 102/182");
	iaView.setCONTEXTDESCRIPTION("Ministry of Health and successors: Miscellaneous Registered Files (99,000 Series). Building and Civil Engineering: National Programme.");
	iaView.setCOVERINGDATES("1952");
	iaView.setTITLE("British National: B.B.C WO100. ");
	iaView.setDESCRIPTION("Labour requirements for the housing programme.");
	iaView.setDOCREFERENCE("C1330010");
	writer.addDocument(LuceneTaxonomyMapper.getLuceneDocumentFromIAView(iaView));

	SearcherManager isearcher = new SearcherManager(writer, true, null);
	iaViewRepository.setIaviewSearcherManager(isearcher);
	return writer;
    }
}

package gov.tna.discovery.taxonomy.common.service.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import gov.tna.discovery.taxonomy.common.config.ServiceConfigurationTest;
import gov.tna.discovery.taxonomy.common.repository.domain.TrainingDocument;
import gov.tna.discovery.taxonomy.common.repository.domain.lucene.InformationAssetView;
import gov.tna.discovery.taxonomy.common.repository.lucene.LuceneTestDataSet;
import gov.tna.discovery.taxonomy.common.repository.lucene.TrainingSetRepository;
import gov.tna.discovery.taxonomy.common.repository.mongo.MongoTestDataSet;
import gov.tna.discovery.taxonomy.common.repository.mongo.TrainingDocumentRepository;
import gov.tna.discovery.taxonomy.common.service.CategoriserService;
import gov.tna.discovery.taxonomy.common.service.domain.CategorisationResult;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.ReaderManager;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ServiceConfigurationTest.class)
public class CategoriserTest {
    // private static final Logger logger =
    // LoggerFactory.getLogger(CategoriserTest.class);

    @Autowired
    CategoriserService categoriser;

    @Autowired
    TrainingDocumentRepository trainingDocumentRepository;

    @Autowired
    private TrainingSetRepository trainingSetRepository;

    @Autowired
    MongoTestDataSet mongoTestDataSet;

    @Autowired
    private SearcherManager trainingSetSearcherManager;

    @Autowired
    private Analyzer trainingSetAnalyser;

    @Autowired
    private Directory trainingSetDirectory;

    @Value("${lucene.index.version}")
    private String luceneVersion;

    @Autowired
    private LuceneTestDataSet luceneTestDataSet;

    @Before
    public void initDataSet() throws IOException {
	mongoTestDataSet.initTrainingSetCollection();
	for (TrainingDocument trainingDocument : trainingDocumentRepository.findAll()) {
	    trainingSetRepository.indexTrainingDocuments(Arrays.asList(trainingDocument));
	}
    }

    @After
    public void emptyDataSet() throws IOException {
	mongoTestDataSet.dropDatabase();
	luceneTestDataSet.deleteTrainingSetIndex();
    }

    @Test
    public void testCategoriseIAViewSolrDocument() throws IOException, ParseException {
	List<CategorisationResult> categorisationResults = categoriser.categoriseIAViewSolrDocument("CO 273/630/9");
	assertThat(categorisationResults, is(notNullValue()));
	assertThat(categorisationResults, is(not(empty())));
    }

    @Test
    public void testTestCategoriseSingle() {
	InformationAssetView iaView = new InformationAssetView();
	iaView.setCATDOCREF("TEST");
	iaView.setDESCRIPTION("Singapore Harbour Board: indemnity against any damage caused by explosives on board HM ships in harbour area.");
	List<CategorisationResult> categorisationResults = categoriser.testCategoriseSingle(iaView);
	assertThat(categorisationResults, is(notNullValue()));
	assertThat(categorisationResults, is(not(empty())));
	assertThat(categorisationResults.get(0).getName(), is(equalTo("Resources")));

    }

    @Test
    public void testTestCategoriseSingleWithIncompleteDescription() {
	InformationAssetView iaView = new InformationAssetView();
	iaView.setDESCRIPTION("Singapore Harbour Board: indemnity against any damage caused by explosives");
	List<CategorisationResult> categorisationResults = categoriser.testCategoriseSingle(iaView);
	assertThat(categorisationResults, is(notNullValue()));
	assertThat(categorisationResults, is(not(empty())));
	assertThat(categorisationResults.get(0).getName(), is(equalTo("Migration")));
    }

    @Test
    public final void testTestCategoriseSingleDocumentWithMoreFields() {
	InformationAssetView iaView = new InformationAssetView();
	iaView.setTITLE("UK bilateral aid programme: review by Ministry of Overseas Development working party; papers, minutes and correspondance");
	iaView.setDESCRIPTION("UK bilateral aid programme: review by Ministry of Overseas Development working party; papers, minutes and correspondence.");
	iaView.setCONTEXTDESCRIPTION("Board of Trade and successors: Export Policy and Promotion Division and successors: Registered Files (XP Series).");

	List<CategorisationResult> categorisationResults = categoriser.testCategoriseSingle(iaView);
	assertThat(categorisationResults, is(notNullValue()));
	assertThat(categorisationResults, is(not(empty())));
    }

    // FIXME to fix
    @Test
    @Ignore
    public void testCategorisationTakesIntoAccountLatestAddsToIndex() throws IOException {
	try {

	    checkCategorisationOnTestDocumentDoesNotFindTestCategory();

	    luceneTestDataSet.updateTrainingSetForTestCategory();
	    trainingSetSearcherManager.maybeRefreshBlocking();

	    checkCategorisationOnTestDocumentFindsTestCategory();
	} finally {
	    luceneTestDataSet.deleteTrainingSetForTestCategory();
	}
    }

    private void checkCategorisationOnTestDocumentDoesNotFindTestCategory() {
	InformationAssetView iaView = new InformationAssetView();
	iaView.setDESCRIPTION(LuceneTestDataSet.TEST_DESC);
	iaView.setDOCREFERENCE(LuceneTestDataSet.TEST_DOCREF);
	List<CategorisationResult> categorisationResults = categoriser.testCategoriseSingle(iaView);
	assertThat(categorisationResults, is(notNullValue()));
	assertThat(categorisationResults, is(empty()));
    }

    private void checkCategorisationOnTestDocumentFindsTestCategory() {
	InformationAssetView iaView = new InformationAssetView();
	iaView.setDESCRIPTION(LuceneTestDataSet.TEST_DESC);
	iaView.setDOCREFERENCE(LuceneTestDataSet.TEST_DOCREF);
	List<CategorisationResult> categorisationResults = categoriser.testCategoriseSingle(iaView);
	assertThat(categorisationResults, is(notNullValue()));
	assertThat(categorisationResults, is(not(empty())));
	assertThat(categorisationResults.get(0).getName(), is(equalTo(LuceneTestDataSet.TEST_CATEGORY)));
    }

}

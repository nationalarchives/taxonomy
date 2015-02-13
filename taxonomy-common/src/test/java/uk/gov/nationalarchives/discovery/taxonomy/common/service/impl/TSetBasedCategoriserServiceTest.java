package uk.gov.nationalarchives.discovery.taxonomy.common.service.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.gov.nationalarchives.discovery.taxonomy.common.config.ServiceConfigurationTest;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.TrainingDocument;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetView;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.TSetBasedCategorisationResult;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.LuceneTestTrainingDataSet;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.TrainingSetRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.MongoTestDataSet;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.TrainingDocumentRepository;

@ActiveProfiles("tsetBased")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ServiceConfigurationTest.class)
public class TSetBasedCategoriserServiceTest {
    // private static final Logger logger =
    // LoggerFactory.getLogger(CategoriserTest.class);

    @Autowired
    TSetBasedCategoriserServiceImpl categoriser;

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
    private LuceneTestTrainingDataSet luceneTestDataSet;

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
    public void testTestCategoriseSingle() {
	InformationAssetView iaView = new InformationAssetView();
	iaView.setDESCRIPTION("Singapore Harbour Board: indemnity against any damage caused by explosives on board HM ships in harbour area.");
	List<TSetBasedCategorisationResult> categorisationResults = categoriser.testCategoriseSingle(iaView);
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

	List<TSetBasedCategorisationResult> categorisationResults = categoriser.testCategoriseSingle(iaView);
	assertThat(categorisationResults, is(notNullValue()));
	assertThat(categorisationResults, is(not(empty())));
    }

    // FIXME 1 must take into account latest adds to index
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
	iaView.setDESCRIPTION(LuceneTestTrainingDataSet.TEST_DESC);
	iaView.setDOCREFERENCE(LuceneTestTrainingDataSet.TEST_DOCREF);
	List<TSetBasedCategorisationResult> categorisationResults = categoriser.testCategoriseSingle(iaView);
	assertThat(categorisationResults, is(notNullValue()));
	assertThat(categorisationResults, is(empty()));
    }

    private void checkCategorisationOnTestDocumentFindsTestCategory() {
	InformationAssetView iaView = new InformationAssetView();
	iaView.setDESCRIPTION(LuceneTestTrainingDataSet.TEST_DESC);
	iaView.setDOCREFERENCE(LuceneTestTrainingDataSet.TEST_DOCREF);
	List<TSetBasedCategorisationResult> categorisationResults = categoriser.testCategoriseSingle(iaView);
	assertThat(categorisationResults, is(notNullValue()));
	assertThat(categorisationResults, is(not(empty())));
	assertThat(categorisationResults.get(0).getName(), is(equalTo(LuceneTestTrainingDataSet.TEST_CATEGORY)));
    }

}

package gov.tna.discovery.taxonomy.service.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import gov.tna.discovery.taxonomy.config.ServiceConfigurationTest;
import gov.tna.discovery.taxonomy.repository.domain.lucene.InformationAssetView;
import gov.tna.discovery.taxonomy.repository.mongo.MongoTestDataSet;
import gov.tna.discovery.taxonomy.repository.mongo.TrainingDocumentRepository;
import gov.tna.discovery.taxonomy.service.Categoriser;
import gov.tna.discovery.taxonomy.service.domain.CategorisationResult;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ServiceConfigurationTest.class)
public class CategoriserTest {
    // private static final Logger logger =
    // LoggerFactory.getLogger(CategoriserTest.class);

    @Autowired
    Categoriser categoriser;

    @Autowired
    TrainingDocumentRepository trainingDocumentRepository;

    @Autowired
    MongoTestDataSet mongoTestDataSet;

    @Before
    public void initDataSet() throws IOException {
	mongoTestDataSet.initCategoryCollection();
	mongoTestDataSet.initTrainingSetCollection();
    }

    @After
    public void emptyDataSet() throws IOException {
	mongoTestDataSet.dropDatabase();
    }

    @Test
    // FIXME need to add data set for that method. it changes after every
    // training set index update
    public void testCategoriseIAViewSolrDocument() throws IOException, ParseException {
	List<CategorisationResult> categorisationResults = categoriser.categoriseIAViewSolrDocument("CO 273/630/9");
	assertThat(categorisationResults, is(notNullValue()));
	assertThat(categorisationResults, is(not(empty())));
    }

    @Test
    public void testTestCategoriseSingle() {
	InformationAssetView iaView = new InformationAssetView();
	iaView.setDESCRIPTION("Singapore Harbour Board: indemnity against any damage caused by explosives on board HM ships in harbour area.");
	List<CategorisationResult> categorisationResults = categoriser.testCategoriseSingle(iaView);
	assertThat(categorisationResults, is(notNullValue()));
	assertThat(categorisationResults, is(not(empty())));
	assertThat(categorisationResults.get(0).getName(), is(equalTo("Migration")));

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

}

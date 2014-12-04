package gov.tna.discovery.taxonomy.service.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import gov.tna.discovery.taxonomy.repository.domain.lucene.InformationAssetView;
import gov.tna.discovery.taxonomy.repository.mongo.TrainingDocumentRepository;
import gov.tna.discovery.taxonomy.service.Categoriser;

import java.io.IOException;
import java.util.Map;

import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Test
    // FIXME need to add data set for that method. it changes after every
    // training set index update
    public void testCategoriseIAViewSolrDocument() throws IOException, ParseException {
	Map<String, Float> categories = categoriser.categoriseIAViewSolrDocument("CO 273/630/9");
	assertThat(categories, is(notNullValue()));
	assertThat(categories.keySet(), is(not(empty())));
    }

    @Test
    public void testTestCategoriseSingle() {
	InformationAssetView iaView = new InformationAssetView();
	iaView.setDESCRIPTION("Singapore Harbour Board: indemnity against any damage caused by explosives on board HM ships in harbour area.");
	Map<String, Float> mapOfCategoriesAndScores = categoriser.testCategoriseSingle(iaView);
	assertThat(mapOfCategoriesAndScores, is(notNullValue()));
	assertThat(mapOfCategoriesAndScores.keySet(), is(not(empty())));

    }

    @Test
    public void testTestCategoriseSingleWithIncompleteDescription() {
	InformationAssetView iaView = new InformationAssetView();
	iaView.setDESCRIPTION("Singapore Harbour Board: indemnity against any damage caused by explosives");
	Map<String, Float> mapOfCategoriesAndScores = categoriser.testCategoriseSingle(iaView);
	assertThat(mapOfCategoriesAndScores, is(notNullValue()));
	assertThat(mapOfCategoriesAndScores.keySet(), is(not(empty())));

    }

    @Test
    public final void testTestCategoriseSingleDocumentWithMoreFields() {
	InformationAssetView iaView = new InformationAssetView();
	iaView.setTITLE("UK bilateral aid programme: review by Ministry of Overseas Development working party; papers, minutes and correspondance");
	iaView.setDESCRIPTION("UK bilateral aid programme: review by Ministry of Overseas Development working party; papers, minutes and correspondence.");
	iaView.setCONTEXTDESCRIPTION("Board of Trade and successors: Export Policy and Promotion Division and successors: Registered Files (XP Series).");
	iaView.setCOVERINGDATES("1969");

	Map<String, Float> mapOfCategoriesAndScores = categoriser.testCategoriseSingle(iaView);
	assertThat(mapOfCategoriesAndScores, is(notNullValue()));
	assertThat(mapOfCategoriesAndScores.keySet(), is(not(empty())));

    }

}

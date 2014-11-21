package gov.tna.discovery.taxonomy.service.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import gov.tna.discovery.taxonomy.ConfigurationTest;
import gov.tna.discovery.taxonomy.repository.domain.lucene.InformationAssetView;
import gov.tna.discovery.taxonomy.repository.domain.lucene.InformationAssetViewFields;
import gov.tna.discovery.taxonomy.repository.mongo.TrainingDocumentRepository;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ConfigurationTest.class)
public class CategoriserTest {
    private static final Logger logger = LoggerFactory.getLogger(CategoriserTest.class);

    @Autowired
    Categoriser categoriser;

    @Autowired
    TrainingDocumentRepository trainingDocumentRepository;

    @Autowired
    IndexReader iaViewIndexReader;

    @Test
    // FIXME need to add data set for that method. it changes after every
    // training set index update
    public void testCategoriseIAViewSolrDocument() throws IOException, ParseException {
	Map<String, Float> categories = categoriser.categoriseIAViewSolrDocument("CO 273/630/9");
	assertThat(categories, is(notNullValue()));
	assertThat(categories.size(), is(equalTo(3)));
    }

    @Test
    public void testTestCategoriseSingle() {
	InformationAssetView iaView = new InformationAssetView();
	iaView.setDESCRIPTION("Singapore Harbour Board: indemnity against any damage caused by explosives on board HM ships in harbour area.");
	Map<String, Float> mapOfCategoriesAndScores = categoriser.testCategoriseSingle(iaView);
	assertThat(mapOfCategoriesAndScores, is(notNullValue()));
	assertThat(mapOfCategoriesAndScores.size(), is(equalTo(3)));
	assertThat(mapOfCategoriesAndScores.containsKey("Mental illness"), is(true));

    }

    /**
     * Used to find document having categories for another test case. Do not
     * activate it
     * 
     * @throws IOException
     * @throws ParseException
     */
    @Test
    @Ignore
    public void toolFindDocumentWithCategory() throws IOException, ParseException {
	findDocloop: for (int i = 0; i < 2000; i++) {
	    if (this.iaViewIndexReader.hasDeletions()) {
		System.out
			.println("[ERROR].categoriseDocument: the reader provides deleted elements though it should not");
	    }

	    Document doc = this.iaViewIndexReader.document(i);

	    Categoriser categoriser = new Categoriser();
	    Reader reader = new StringReader(doc.get(InformationAssetViewFields.DESCRIPTION.toString()));
	    Map<String, Float> result = categoriser.runMlt(reader);

	    logger.debug("DOCUMENT");
	    logger.debug("------------------------");
	    logger.debug("TITLE: " + doc.get("TITLE"));
	    logger.debug("IAID: " + doc.get("CATDOCREF"));
	    logger.debug("DESCRIPTION: " + doc.get("DESCRIPTION"));
	    logger.debug("");
	    for (Entry<String, Float> category : result.entrySet()) {
		logger.debug("DOC TITLE: {}, IAID: {}, CATEGORY: {}, SCORE: {}", doc.get("TITLE"),
			doc.get("CATDOCREF"), category.getKey(), category.getValue());
		break findDocloop;
	    }
	    logger.debug("------------------------");

	    logger.debug("");

	}

	logger.debug("Categorisation finished");

    }

}

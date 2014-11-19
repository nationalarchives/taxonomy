package gov.tna.discovery.taxonomy.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gov.tna.discovery.taxonomy.ConfigurationTest;
import gov.tna.discovery.taxonomy.config.CatConstants;
import gov.tna.discovery.taxonomy.repository.domain.lucene.InformationAssetViewFields;
import gov.tna.discovery.taxonomy.repository.lucene.Indexer;
import gov.tna.discovery.taxonomy.repository.mongo.TrainingDocumentRepository;
import gov.tna.discovery.taxonomy.service.impl.Categoriser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ConfigurationTest.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
// TODO generate memory db with data set for testing
public class CategoriserTest {
    private static final Logger logger = LoggerFactory.getLogger(Indexer.class);

    @Autowired
    Categoriser categoriser;

    @Autowired
    TrainingDocumentRepository trainingDocumentRepository;

    @Test
    // FIXME need to add data set for that method. it changes after every
    // training set index update
    public void test3CategoriseIAViewSolrDocument() throws IOException, ParseException {
	List<String> result = categoriser.categoriseIAViewSolrDocument("CO 273/630/9");
	assertNotNull(result);
	assertEquals(3, result.size());
    }

    @Test
    @Ignore
    public void toolFindDocumentWithCategory() throws IOException, ParseException {
	Indexer indexer = new Indexer();
	IndexReader indexReader = indexer.getIndexReader(CatConstants.IAVIEW_INDEX);
	findDocloop: for (int i = 0; i < indexReader.maxDoc(); i++) {
	    // TODO 2 Add concurrency: categorize several documents at the same
	    // time
	    if (indexReader.hasDeletions()) {
		System.out
			.println("[ERROR].categoriseDocument: the reader provides deleted elements though it should not");
	    }

	    Document doc = indexReader.document(i);

	    Categoriser categoriser = new Categoriser();
	    Reader reader = new StringReader(doc.get(InformationAssetViewFields.DESCRIPTION.toString()));
	    List<String> result = categoriser.runMlt(CatConstants.TRAINING_INDEX, reader, 100);

	    logger.debug("DOCUMENT");
	    logger.debug("------------------------");
	    logger.debug("TITLE: " + doc.get("TITLE"));
	    logger.debug("IAID: " + doc.get("CATDOCREF"));
	    logger.debug("DESCRIPTION: " + doc.get("DESCRIPTION"));
	    logger.debug("");
	    for (String category : result) {
		logger.debug("CATEGORY: " + category);
		break findDocloop;
	    }
	    logger.debug("------------------------");

	    logger.debug("");

	}

	logger.debug("Categorisation finished");

    }

}

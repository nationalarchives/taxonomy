package taxonomy.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import taxonomy.repository.mongo.MongoAccess;

import com.mongodb.DBCollection;

public class CategoriserTest {

    private static final Logger logger = LoggerFactory.getLogger(CategoriserTest.class);

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testCreateTrainingSet() throws IOException, ParseException {
	Categoriser.createTrainingSet(1);
	MongoAccess mongoAccess = new MongoAccess();
	DBCollection dBCollection = mongoAccess.getMongoCollection("taxonomy", "trainingset");
	assertEquals(115, dBCollection.getStats().getInt("count"));
    }

    @Test
    @Ignore
    public void testIndexTrainingSet() {
	fail("Not yet implemented");
    }

    @Test
    // FIXME need to add data set for that method. it changes after every
    // training set index update
    public void testCategoriseIAViewSolrDocument() throws IOException, ParseException {
	List<String> result = Categoriser.categoriseIAViewSolrDocument("ADM 106/1172/81");
	assertNotNull(result);
	assertEquals(6, result.size());
	;
    }

    @Test
    @Ignore
    public void testCategoriseIAViewsFromSolr() {
	fail("Not yet implemented");
    }

}

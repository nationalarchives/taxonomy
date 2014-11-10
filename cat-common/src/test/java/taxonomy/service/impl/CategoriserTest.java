package taxonomy.service.impl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import taxonomy.repository.mongo.MongoAccess;

import com.mongodb.DBCollection;

public class CategoriserTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testCreateTrainingSet() throws IOException, ParseException {
		Categoriser.createTrainingSet(1);
		MongoAccess mongoAccess = new MongoAccess();
		DBCollection dBCollection = mongoAccess.getMongoCollection(
				"taxonomy", "trainingset");
		assertEquals(115, dBCollection.getStats().getInt("count"));
	}

	@Test
	@Ignore
	public void testIndexTrainingSet() {
		fail("Not yet implemented");
	}

	@Test
	public void testCategoriseIAViewSolrDocument() throws IOException, ParseException {
		List<String> result = Categoriser.categoriseIAViewSolrDocument("CO 273/632/2");
		assertNotNull(result);
		assertEquals(17, result.size());;
	}

	@Test
	@Ignore
	public void testCategoriseIAViewsFromSolr() {
		fail("Not yet implemented");
	}

}

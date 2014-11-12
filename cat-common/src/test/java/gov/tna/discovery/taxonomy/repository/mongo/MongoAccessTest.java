package gov.tna.discovery.taxonomy.repository.mongo;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.mongodb.Mongo;

public class MongoAccessTest {

	private MongoAccess mongoAccess;

	@Before
	public void initDb(){
		mongoAccess = new MongoAccess();
	}
	
	@Test
	@Ignore
	public void testConnectToDbDevPlatform() {
		Mongo mongo = mongoAccess.connectToMongoDb("***REMOVED***.***REMOVED***", 27017);
		assertNotNull(mongo);
	}

}

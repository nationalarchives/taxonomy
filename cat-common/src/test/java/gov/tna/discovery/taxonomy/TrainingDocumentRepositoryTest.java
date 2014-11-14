package gov.tna.discovery.taxonomy;

import static org.junit.Assert.assertEquals;
import gov.tna.discovery.taxonomy.repository.mongo.TrainingDocumentRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes=MongoConfigurationTest.class)
public class TrainingDocumentRepositoryTest{

    @Autowired
    TrainingDocumentRepository repository;
    
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testCollectionCount() {
	assertEquals(115l, repository.count());
    }

}

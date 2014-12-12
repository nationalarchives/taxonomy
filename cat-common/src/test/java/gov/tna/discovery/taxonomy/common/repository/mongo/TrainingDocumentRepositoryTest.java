package gov.tna.discovery.taxonomy.common.repository.mongo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import gov.tna.discovery.taxonomy.common.repository.domain.TrainingDocument;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MongoConfigurationTest.class)
public class TrainingDocumentRepositoryTest {

    @Autowired
    TrainingDocumentRepository repository;

    @Autowired
    MongoTestDataSet mongoTestDataSet;

    @Before
    public void initDataSet() throws IOException {
	mongoTestDataSet.initTrainingSetCollection();
    }

    @After
    public void emptyDataSet() throws IOException {
	mongoTestDataSet.dropDatabase();
    }

    @Test
    public void testCollectionCount() {

	Iterable<TrainingDocument> iterable = repository.findAll();
	assertThat(iterable, is(notNullValue()));
	assertThat(iterable.iterator().hasNext(), is(true));
    }

}

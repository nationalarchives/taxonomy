package gov.tna.discovery.taxonomy.service.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.either;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import gov.tna.discovery.taxonomy.config.ServiceConfigurationTest;
import gov.tna.discovery.taxonomy.repository.mongo.MongoTestDataSet;
import gov.tna.discovery.taxonomy.repository.mongo.TrainingDocumentRepository;
import gov.tna.discovery.taxonomy.service.TrainingSetService;

import java.io.IOException;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.ReaderManager;
import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ServiceConfigurationTest.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TrainingSetServiceTest {
    // private static final Logger logger =
    // LoggerFactory.getLogger(Indexer.class);

    @Autowired
    TrainingSetService trainingSetService;

    @Autowired
    TrainingDocumentRepository trainingDocumentRepository;

    @Autowired
    ReaderManager trainingSetReaderManager;

    @Autowired
    MongoTestDataSet mongoTestDataSet;

    @Before
    public void initDataSet() throws IOException {
	mongoTestDataSet.initCategoryCollection();
    }

    @After
    public void emptyDataSet() throws IOException {
	mongoTestDataSet.dropDatabase();
    }

    @Test
    public void testCreateTrainingSetWithLimitScore() throws IOException, ParseException {
	trainingSetService.createTrainingSet(0.1f);
	assertEquals(248l, trainingDocumentRepository.count());
    }

    // FIXME this test should not vary
    @Test
    public void testIndexTrainingSet() throws IOException, InterruptedException, ParseException {
	mongoTestDataSet.initTrainingSetCollection();

	trainingSetService.indexTrainingSet();
	DirectoryReader trainingSetIndexReader = trainingSetReaderManager.acquire();
	Thread.sleep(1000);
	assertThat(trainingSetIndexReader.maxDoc(), either(equalTo(220)).or(equalTo(200)));
	trainingSetReaderManager.release(trainingSetIndexReader);
    }
}

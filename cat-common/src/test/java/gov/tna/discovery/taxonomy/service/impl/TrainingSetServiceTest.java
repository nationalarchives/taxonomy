package gov.tna.discovery.taxonomy.service.impl;

import static org.junit.Assert.assertEquals;
import gov.tna.discovery.taxonomy.ConfigurationTest;
import gov.tna.discovery.taxonomy.MongoTestDataSet;
import gov.tna.discovery.taxonomy.repository.mongo.TrainingDocumentRepository;
import gov.tna.discovery.taxonomy.service.Categoriser;
import gov.tna.discovery.taxonomy.service.TrainingSetService;

import java.io.IOException;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.ReaderManager;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ConfigurationTest.class)
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

    // @Before
    // public void initDataSet() throws IOException {
    // mongoTestDataSet.initCategoryCollection();
    // }
    //
    // @After
    // public void emptyDataSet() throws IOException {
    // mongoTestDataSet.dropDatabase();
    // }
    //
    // @Test
    // public void testCreateTrainingSetWithLimitScore() throws IOException,
    // ParseException {
    // trainingSetService.createTrainingSet(0.1f);
    // assertEquals(141l, trainingDocumentRepository.count());
    // }

    // FIXME refresh the solr index periodically in a separate thread and use it
    // here to prevent this test from failing from time to time
    @Test
    public void testIndexTrainingSet() throws IOException, InterruptedException {
	mongoTestDataSet.initTrainingSetCollection();

	trainingSetService.indexTrainingSet();
	DirectoryReader trainingSetIndexReader = trainingSetReaderManager.acquire();
	Thread.sleep(1000);
	assertEquals(141, trainingSetIndexReader.maxDoc());
	trainingSetReaderManager.release(trainingSetIndexReader);
    }
}

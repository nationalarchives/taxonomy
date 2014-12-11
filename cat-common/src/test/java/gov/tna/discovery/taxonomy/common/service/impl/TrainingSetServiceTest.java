package gov.tna.discovery.taxonomy.common.service.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.either;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import gov.tna.discovery.taxonomy.common.config.ServiceConfigurationTest;
import gov.tna.discovery.taxonomy.common.repository.mongo.MongoTestDataSet;
import gov.tna.discovery.taxonomy.common.repository.mongo.TrainingDocumentRepository;
import gov.tna.discovery.taxonomy.common.service.TrainingSetService;

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

    @Test
    public void testCreateTrainingSetWithLimitScore() throws IOException, ParseException {
	mongoTestDataSet.initCategoryCollection();

	trainingSetService.createTrainingSet(0.001f, null);
	assertEquals(17l, trainingDocumentRepository.count());

	mongoTestDataSet.dropDatabase();
    }

    @Test
    public void testCreateTrainingSetWithLimitSize() throws IOException, ParseException {
	mongoTestDataSet.initCategoryCollection();

	trainingSetService.createTrainingSet(null, 1);
	assertEquals(10l, trainingDocumentRepository.count());

	mongoTestDataSet.dropDatabase();
    }

    // FIXME this test should not vary
    // FIXME LUCENE IN MEMORY the trainingset lucene files should be initiated
    // from scratch (and a few documents should be inserted into it)
    // because here, the writer fails to add documents
    @Test
    public void testIndexTrainingSet() throws IOException, InterruptedException, ParseException {
	mongoTestDataSet.initCategoryCollection();
	mongoTestDataSet.initTrainingSetCollection();

	trainingSetService.indexTrainingSet();
	DirectoryReader trainingSetIndexReader = trainingSetReaderManager.acquire();
	Thread.sleep(1000);
	assertThat(trainingSetIndexReader.maxDoc(), equalTo(200));
	trainingSetReaderManager.release(trainingSetIndexReader);

	mongoTestDataSet.dropDatabase();
    }
}

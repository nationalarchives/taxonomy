package gov.tna.discovery.taxonomy.common.service.impl;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import gov.tna.discovery.taxonomy.common.config.ServiceConfigurationTest;
import gov.tna.discovery.taxonomy.common.repository.lucene.LuceneTestDataSet;
import gov.tna.discovery.taxonomy.common.repository.mongo.CategoryRepository;
import gov.tna.discovery.taxonomy.common.repository.mongo.MongoTestDataSet;
import gov.tna.discovery.taxonomy.common.repository.mongo.TrainingDocumentRepository;
import gov.tna.discovery.taxonomy.common.service.TrainingSetService;

import java.io.IOException;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.ReaderManager;
import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
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

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    LuceneTestDataSet luceneTestDataSet;

    @After
    public void dropDbAndIndex() {

	mongoTestDataSet.dropDatabase();
	luceneTestDataSet.deleteTrainingSetIndex();
    }

    @Test
    public void testCreateTrainingSetWithLimitScore() throws IOException, ParseException {
	mongoTestDataSet.initCategoryCollection();

	trainingSetService.createTrainingSet(0.001f, null);
	assertThat(trainingDocumentRepository.count(), equalTo(17l));
    }

    @Test
    public void testCreateTrainingSetWithLimitSize() throws IOException, ParseException {
	mongoTestDataSet.initCategoryCollection();

	trainingSetService.createTrainingSet(null, 1);
	assertThat(trainingDocumentRepository.count(), equalTo(10l));
    }

    @Test
    public void testIndexTrainingSet() throws IOException, InterruptedException, ParseException {
	mongoTestDataSet.initCategoryCollection();
	mongoTestDataSet.initTrainingSetCollection();

	trainingSetService.indexTrainingSet();
	DirectoryReader trainingSetIndexReader = trainingSetReaderManager.acquire();
	Thread.sleep(1000);
	assertThat(trainingSetIndexReader.numDocs(), equalTo(200));
	trainingSetReaderManager.release(trainingSetIndexReader);

    }

    @Test
    public void testDeleteAndUpdateTraingSetIndexForCategory() throws IOException {
	mongoTestDataSet.initCategoryCollection();
	mongoTestDataSet.initTrainingSetCollection();

	trainingSetService.deleteAndUpdateTraingSetIndexForCategory(categoryRepository.findByCiaid("C10052"));

	DirectoryReader trainingSetIndexReader = trainingSetReaderManager.acquire();
	assertThat(trainingSetIndexReader.numDocs(), is(not(equalTo(0))));
    }
}

package gov.tna.discovery.taxonomy.service.impl;

import static org.junit.Assert.assertEquals;
import gov.tna.discovery.taxonomy.ConfigurationTest;
import gov.tna.discovery.taxonomy.repository.mongo.TrainingDocumentRepository;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
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
// TODO generate memory db with data set for testing
public class TrainingSetServiceTest {
    // private static final Logger logger =
    // LoggerFactory.getLogger(Indexer.class);

    @Autowired
    Categoriser categoriser;

    @Autowired
    TrainingSetService trainingSetService;

    @Autowired
    TrainingDocumentRepository trainingDocumentRepository;

    @Autowired
    IndexReader trainingSetIndexReader;

    @Autowired
    private Directory trainingSetDirectory;

    @Autowired
    private IndexWriterConfig indexWriterConfig;

    @Test
    @Ignore
    public void test1CreateTrainingSet() throws IOException, ParseException {
	trainingSetService.createTrainingSet(null);
	assertEquals(5622l, trainingDocumentRepository.count());
    }

    @Test
    public void test2CreateTrainingSetWithLimitScore() throws IOException, ParseException {
	trainingSetService.createTrainingSet(0.1f);
	assertEquals(141l, trainingDocumentRepository.count());
    }

    @Test
    public void test3IndexTrainingSet() throws IOException {
	trainingSetService.indexTrainingSet();
	assertEquals(141, trainingSetIndexReader.maxDoc());
    }
}

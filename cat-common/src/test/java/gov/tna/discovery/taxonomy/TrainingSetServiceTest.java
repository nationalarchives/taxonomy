package gov.tna.discovery.taxonomy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gov.tna.discovery.taxonomy.repository.domain.lucene.InformationAssetViewFields;
import gov.tna.discovery.taxonomy.repository.lucene.Indexer;
import gov.tna.discovery.taxonomy.repository.mongo.TrainingDocumentRepository;
import gov.tna.discovery.taxonomy.service.impl.Categoriser;
import gov.tna.discovery.taxonomy.service.impl.TrainingSetService;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ConfigurationTest.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
// TODO generate memory db with data set for testing
public class TrainingSetServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(Indexer.class);

    @Autowired
    Categoriser categoriser;

    @Autowired
    TrainingSetService trainingSetService;

    @Autowired
    TrainingDocumentRepository trainingDocumentRepository;

    @Test
    @Ignore
    public void test1CreateTrainingSet() throws IOException, ParseException {
	trainingSetService.createTrainingSet(null);
	assertEquals(5622l, trainingDocumentRepository.count());
    }

    @Test
    public void test1CreateTrainingSetWithLimitScore() throws IOException, ParseException {
	trainingSetService.createTrainingSet(0.1f);
	assertEquals(141l, trainingDocumentRepository.count());
    }

    @Test
    public void test2IndexTrainingSet() throws IOException {
	trainingSetService.indexTrainingSet();
	Indexer indexer = new Indexer();
	IndexReader indexReader = indexer.getIndexReader(CatConstants.TRAINING_INDEX);
	assertEquals(141, indexReader.maxDoc());

    }
}

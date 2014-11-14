package gov.tna.discovery.taxonomy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import gov.tna.discovery.taxonomy.repository.lucene.Indexer;
import gov.tna.discovery.taxonomy.repository.mongo.TrainingDocumentRepository;
import gov.tna.discovery.taxonomy.service.impl.Categoriser;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;



@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes=ConfigurationTest.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CategoriserTest {

    @Autowired
    Categoriser categoriser;
    
    @Autowired
    TrainingDocumentRepository trainingDocumentRepository;
    

    @Test
    public void test1CreateTrainingSet() throws IOException, ParseException {
	categoriser.createTrainingSet(1);
	assertEquals(115l, trainingDocumentRepository.count());
    }

    @Test
    public void test2IndexTrainingSet() throws IOException {
	categoriser.indexTrainingSet();
	Indexer indexer = new Indexer();
	IndexReader indexReader = indexer.getIndexReader(CatConstants.TRAINING_INDEX);
	assertEquals(115,indexReader.maxDoc());
		
    }

    @Test
    // FIXME need to add data set for that method. it changes after every
    // training set index update
    public void test3CategoriseIAViewSolrDocument() throws IOException, ParseException {
	List<String> result = categoriser.categoriseIAViewSolrDocument("SP 87/48/9");
	assertNotNull(result);
	assertEquals(5, result.size());
	;
    }

}

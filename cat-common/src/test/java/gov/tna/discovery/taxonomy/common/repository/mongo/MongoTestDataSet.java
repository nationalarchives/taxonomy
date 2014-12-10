package gov.tna.discovery.taxonomy.common.repository.mongo;

import gov.tna.discovery.taxonomy.common.repository.domain.TrainingDocument;
import gov.tna.discovery.taxonomy.common.repository.domain.mongo.Category;
import gov.tna.discovery.taxonomy.common.repository.mongo.CategoryRepository;
import gov.tna.discovery.taxonomy.common.repository.mongo.TrainingDocumentRepository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.lucene.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

@Component
// @ConditionalOnMissingBean(name = "fongo")
public class MongoTestDataSet {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    TrainingDocumentRepository trainingDocumentRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public void initCategoryCollectionWith1element() {
	Category category = new Category();
	category.setCiaid("C10032");
	category.setTtl("Disease");
	category.setQry("\"venereal disease\" OR \"tropical disease\" OR \"industrial disease\" OR \"infectious disease\" OR \"bubonic plague\" OR \"yellow fever\" OR \"malaria\" OR \"tuberculosis\" OR \"scurvy\" OR \"rickets\" OR \"measles\" OR \"influenza\" OR \"bronchitis\" OR \"pneumoconiosis\" OR \"emphysema\" OR \"byssinosis\" OR \"polio\" OR \"dengue fever\" OR \"rabies\" OR \"swine fever\" OR \"weils disease\" OR \"cancer\" OR \"asthma\" OR \"syphilis\" OR \"typhoid\" OR \"gonorrhoea\" OR \"smallpox\" OR \"cholera\" OR \"cholera morbus\" OR \"typhus\" OR \"meningitis\" OR \"dysentery\" OR \"scarlatina\" OR \"scarlet fever\" OR \"pneumonia\" OR \"cynanche tonsillaris\" OR \"synocha\" OR \"opthalmia\" OR \"whooping cough\" OR \"HIV\" OR \"asbestosis\" OR \"mesothelioma\" OR \"beri beri\" OR \"multiple sclerosis\" OR \"diabetes\" OR \"leus venerea\" OR \"leprosy\" OR \"poliomyelitis\" OR \"encephalitis\" OR \"Trypanosomiasis\"");
	category.setLck(false);
	category.setSc(0.001);
	categoryRepository.save(category);
    }

    public void createTrainingSetDocument() {
	TrainingDocument trainingDocument = new TrainingDocument();
	trainingDocument.setDescription("Cloth planning and the clothes ration 1944-1947.");
	trainingDocument.setTitle("Rationing");
	trainingDocumentRepository.save(trainingDocument);
    }

    public void initCategoryCollection() throws IOException {
	URL url = Thread.currentThread().getContextClassLoader().getResource("dataset/taxonomy.json");
	File file = new File(url.getPath());
	FileInputStream fileIs = new FileInputStream(file);
	InputStreamReader isReader = new InputStreamReader(fileIs);
	BufferedReader bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
	String strLine = null;
	DBCollection collection = mongoTemplate.getCollection("categories");
	while ((strLine = bufReader.readLine()) != null) {
	    DBObject bson = (DBObject) JSON.parse(strLine);
	    collection.insert(bson);
	}

	IOUtils.closeWhileHandlingException(fileIs, isReader, bufReader);
    }

    public void initTrainingSetCollection() throws IOException {
	URL url = Thread.currentThread().getContextClassLoader().getResource("dataset/trainingset.json");
	File file = new File(url.getPath());
	FileInputStream fileIs = new FileInputStream(file);
	InputStreamReader isReader = new InputStreamReader(fileIs);
	BufferedReader bufReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
	String strLine = null;
	DBCollection collection = mongoTemplate.getCollection("trainingset");
	while ((strLine = bufReader.readLine()) != null) {
	    DBObject bson = (DBObject) JSON.parse(strLine);
	    collection.insert(bson);
	}

	IOUtils.closeWhileHandlingException(fileIs, isReader, bufReader);
    }

    public void dropDatabase() {
	mongoTemplate.dropCollection(Category.class);
	mongoTemplate.dropCollection(TrainingDocument.class);
    }
}

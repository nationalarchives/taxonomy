package gov.tna.discovery.taxonomy.common.repository.mongo;

import gov.tna.discovery.taxonomy.common.repository.domain.TrainingDocument;
import gov.tna.discovery.taxonomy.common.repository.domain.mongo.Category;
import gov.tna.discovery.taxonomy.common.repository.lucene.LuceneTestDataSet;
import gov.tna.discovery.taxonomy.common.repository.mongo.CategoryRepository;
import gov.tna.discovery.taxonomy.common.repository.mongo.TrainingDocumentRepository;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.lucene.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

@Component
public class MongoTestDataSet {

    @Value("${spring.data.mongo.testdataset.trainingset}")
    private String trainingSetDatasetFilePath;

    @Value("${spring.data.mongo.testdataset.categories}")
    private String categoriesDatasetFilePath;

    private static final Logger logger = LoggerFactory.getLogger(LuceneTestDataSet.class);

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    TrainingDocumentRepository trainingDocumentRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    public void initCategoryCollectionWith1element() {
	logger.info(".initCategoryCollectionWith1element");
	Category category = new Category();
	category.setCiaid("C10032");
	category.setTtl("Labour");
	category.setQry("\"manpower\" OR \"employment\" OR \"employer\" OR \"employers\" OR \"worker\" OR \"workers\" OR \"workpeople\" OR \"workforce\" OR \"labourers\" OR \"jobs\" OR \"job creation\" OR (\"labour employment\"~5) OR \"labour policy\" OR \"labour market\" OR \"labour markets\" OR \"labour force\" OR \"labour demand\" OR \"labour demands\" OR \"labour supply\" OR \"labour requirement\" OR \"labour requirements\" OR \"labour economic\" OR \"labour economics\" OR \"labour productivity\" OR \"labour condition\" OR \"labour conditions\" OR \"working condition\" OR \"working conditions\" OR (\"condition labour\"~3) OR (\"conditions labour\"~3) OR (\"work environment\"~1) OR (\"working environment\"~1) OR \"labour contract\" OR \"labour contracts\" OR \"labour organisation\" OR \"labour organisations\" OR \"international labour\" OR \"ILO\" OR \"I.L.O\" OR \"agricultural labour\" OR \"industrial labour\" OR \"dock labour\" OR \"voluntary labour\" OR \"shift labour\" OR \"part-time labour\" OR \"self-employed labour\" OR \"youth labour\" OR \"labour law\" OR \"labour laws\" OR \"labour legislation\" OR \"labour statistic\" OR \"labour statistics\" OR \"labour exchange\" OR \"labour exchanges\" OR \"Jobcentre\" OR \"Jobcentres\" OR \"Remploy\" OR \"job seekers\" OR \"job seeker\" OR \"job seeking\" OR \"labour migration\" OR \"labour mobility\" OR \"labour movement\" OR \"labour movements\" OR \"alien labour\" OR \"migrant labour\" OR \"indentured labour\" OR \"compulsory labour\" OR \"forced labour\" OR \"work permit\" OR \"work permits\" OR \"collective labour\" OR \"collective agreement\" OR \"collective agreements\" OR \"collective bargaining\" OR \"industrial democracy\" OR \"productivity bargain*\" OR \"labour relation*\" OR (\"conciliation labour\"~5) OR (\"arbitration labour\"~5) OR \"Advisory, Conciliation and Arbitration Service\" OR \"ACAS\" OR \"A.C.A.S\" OR \"labour conflict\" OR \"labour conflicts\" OR \"industrial stoppage\" OR \"industrial stoppages\" OR \"labour dispute\" OR \"labour disputes\" OR \"trade dispute\" OR \"trade disputes\" OR \"pay dispute\" OR \"pay strike\" OR \"pay strikes\" OR \"unofficial strikes\" OR \"unofficial strikes\" OR \"official strike\" OR \"official strikes\" OR \"General strike\" OR \"national strike\" OR (\"coal strike\"~2) OR (\"coal strikes\"~2) OR (\"strike mineworker\"~2) OR (\"strike mineworkers\"~2) OR (\"strike miner\"~2) OR (\"strike miners\"~2) OR (\"rail strike\"~2) OR (\"railway strikes\"~2) OR (\"transport strike\"~2) OR (\"transport strikes\"~2) OR \"police strike\" OR (\"dock strike\"~2) OR (\"docker strike\"~2) OR (\"dockers strike\"~2) OR (\"dock strikes\"~2) OR (\"docker strikes\"~2) OR (\"dockers strikes\"~2) OR (\"strikes industrial\"~2) OR (\"strike industrial\"~2) OR (*workers AND \"strike\"~2) OR (*workers AND \"strikes\"~2) OR (*makers AND strike\"~2) OR (*makers AND strikes\"~2) OR (*builders AND \"strike\"~2) OR (*builders AND \"strikes\"~2) OR (\"officers strike\"~2) OR (\"officers strikes\"~2) OR (\"officials strike\"~2) OR (\"officials strikes\"~2) OR (\"labourers strike\"~2) OR (\"labourers strikes\"~2) OR (\"staff strike\"~2) OR (\"staff strikes\"~2) OR (\"engineers strike\"~2) OR (\"engineers strikes\"~2) OR (\"spinners strike\"~2) OR (\"spinners strikes\"~2) OR (*fitter AND \"strike\"~2) OR (*fitters AND \"strike\"~2) OR (*fitter AND \"strikes\"~2) OR (*fitters AND \"strikes\"~2) OR (\"manufacturers strike\"~2) OR (\"manufacturers strikes\"~2) OR (\"machinists strike\"~2) OR (\"machinists strikes\"~2) OR (\"sailors strike\"~2) OR (\"sailors strikes\"~2) OR (\"weavers strike\"~2) OR (\"weavers strikes\"~2) OR (\"employees strike\"~2) OR (\"employees strikes\"~2) OR (\"handlers strike\"~2) OR (\"handlers strikes\"~2) OR (\"seamen strike\"~2) OR (\"seamen strikes\"~2) OR (\"seamens strike\"~2) OR (\"seamens strikes\"~2) OR (\"seaman strike\"~2) OR (\"seaman strikes\"~2) OR (\"seamans strike\"~2) OR (\"seamans strikes\"~2) OR (\"postman strike\"~2) OR (\"postman strikes\"~2) OR (\"postmans strike\"~2) OR (\"postmans strikes\"~2) OR (\"postmen strike\"~2) OR (\"postmen strikes\"~2) OR (\"postmens strike\"~2) OR (\"postmens strikes\"~2) OR \"strike ballot*\" OR \"strike action\" OR \"industrial action\" OR \"work to rule\" OR \"pickets\" OR \"picketing\" OR \"threatened strike\" OR \"threatened strikes\" OR \"trade union\" OR \"trade unions\" OR \"trades union\" OR \"trades unions\" OR \"industrial unionism\" OR \"industrial relation\" OR \"industrial relations\" OR \"TUC\" OR \"T.U.C\" OR \"shop steward\" OR \"shop stewards\" OR \"conciliation officer\" OR \"conciliation officers\" OR \"labour court\" OR \"labour courts\" OR \"Whitley Council\" OR \"Whitley Councils\" OR \"industrial tribunal\" OR \"industrial tribunals\" OR \"arbitration tribunal\" OR \"arbitration tribunals\" OR \"staff association\" OR \"staff associations\" OR \"worker participation\" OR \"workers participation\" OR (\"women labour\"~2) OR (\"womens labour\"~2) OR \"female labour\" OR \"male labour\" OR \"boy labour\" OR \"boy labourer\" OR \"boy labourers\" OR \"child labour\" OR \"labor\" OR \"underemployment\" OR \"unemployment\" OR \"apprentice\" OR \"apprentices\" OR \"foreman\" OR \"foremen\" OR \"labour and predecessors\" NOT \"Labour Party\" NOT \"Labour Government\" NOT \"Labour opposition\" NOT (\"labour birth\"~3) NOT \"HMS Striker\" NOT \"lucky strike\" NOT \"hunger strike\" NOT \"hunger striker\" NOT \"hunger strikers\" NOT \"hunger striking\" NOT \"Air Staff\" NOT \"strike aircraft\"");
	category.setLck(false);
	category.setSc(0.001);
	categoryRepository.save(category);
    }

    public void createTrainingSetDocument() {
	logger.info(".createTrainingSetDocument");
	TrainingDocument trainingDocument = new TrainingDocument();
	trainingDocument.setDescription("Cloth planning and the clothes ration 1944-1947.");
	trainingDocument.setTitle("Rationing");
	trainingDocumentRepository.save(trainingDocument);
    }

    public void initCategoryCollection() throws IOException {
	logger.info(".initCategoryCollection");
	File file = new File(categoriesDatasetFilePath);
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
	logger.info(".initTrainingSetCollection");
	File file = new File(trainingSetDatasetFilePath);
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
	logger.info(".dropDatabase");
	mongoTemplate.dropCollection(Category.class);
	mongoTemplate.dropCollection(TrainingDocument.class);
    }
}

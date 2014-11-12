package gov.tna.discovery.taxonomy.repository.mongo;

import gov.tna.discovery.taxonomy.CatConstants;
import gov.tna.discovery.taxonomy.repository.domain.Category;
import gov.tna.discovery.taxonomy.repository.domain.CategoryFields;
import gov.tna.discovery.taxonomy.repository.domain.InformationAsset;
import gov.tna.discovery.taxonomy.repository.domain.InformationAssetFields;
import gov.tna.discovery.taxonomy.repository.domain.InformationAssetViewFields;
import gov.tna.discovery.taxonomy.repository.domain.TrainingDocument;
import gov.tna.discovery.taxonomy.service.impl.Categoriser;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;

public class MongoAccess {
//    private static final Logger logger = LoggerFactory.getLogger(MongoAccess.class);

    public DBCollection getMongoCollection(String database, String collection) {
	Mongo conn = connectToMongoDb(CatConstants.MONGO_HOST, CatConstants.MONGO_PORT);
	DB db = conn.getDB(database);
	DBCollection dbCollection = db.getCollection(collection);
	// conn.close();
	return dbCollection;
    }

    public Mongo connectToMongoDb(String host, int port) {
	Mongo conn;
	try {
	    conn = new Mongo(host, port);
	} catch (Exception e) {
	    throw new RuntimeException(e);
	}
	WriteConcern w = new WriteConcern(1, 2000);
	conn.setWriteConcern(w);
	return conn;
    }

    public InformationAsset getInformationAsset(String catdocref) {

	DBCollection dbCollection = getMongoCollection(CatConstants.MONGO_IA_DB, CatConstants.MONGO_IA_COLL);
	BasicDBObject query = new BasicDBObject();
	query.put("IAID", catdocref);
	DBObject doc = dbCollection.findOne(query);
	InformationAsset informationAsset = new InformationAsset();
	informationAsset.set_id(doc.get(InformationAssetFields._id.toString()).toString());
	informationAsset.setCatdocref(doc.get(InformationAssetFields.IAID.toString()).toString());
	DBObject scopecontent = (BasicDBObject) doc.get(InformationAssetFields.ScopeContent.toString());
	String description = (String) scopecontent.get(InformationAssetFields.Description.toString());
	informationAsset.setDescription(description);
	return informationAsset;
    }

    public List<Category> getCategories() throws MongoException {

	List<Category> categories = new ArrayList<Category>();
	DBCollection dbCollection = getMongoCollection(CatConstants.MONGO_TAXONOMY_DB, CatConstants.MONGO_CAT_COLL);
	DBCursor cursor = dbCollection.find();
	for (DBObject doc : cursor) {
	    Category category = new Category();
	    category.set_id(doc.get(CategoryFields._id.toString()).toString());
	    category.setCategory(doc.get(CategoryFields.ttl.toString()).toString());
	    category.setQUERY(doc.get(CategoryFields.qry.toString()).toString());
	    categories.add(category);
	}
	cursor.close();
	return categories;
    }

    public void addTrainingDocument(TrainingDocument trainingDocument, DBCollection dBCollection) {
	BasicDBObject doc = new BasicDBObject();
	doc.put(InformationAssetViewFields.CATEGORY.toString(), trainingDocument.getCategory());
	doc.put(InformationAssetViewFields.DESCRIPTION.toString(),
		trainingDocument.getDescription().replaceAll("\\<.*?>", ""));
	doc.put(InformationAssetViewFields.TITLE.toString(), trainingDocument.getTitle().replaceAll("\\<.*?>", ""));
	dBCollection.insert(doc);
    }
}

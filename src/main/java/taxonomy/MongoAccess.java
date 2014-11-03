package taxonomy;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;

public class MongoAccess {

	public DBCollection connectToDb(String database, String collection) {
		Mongo conn;
		try {
			conn = new Mongo("localhost", 27017);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		WriteConcern w = new WriteConcern(1, 2000);
		conn.setWriteConcern(w);
		DB db = conn.getDB(database);
		DBCollection dbCollection = db.getCollection(collection);
		// conn.close();
		return dbCollection;
	}

	public InformationAsset getInformationAsset(String catdocref) {

		DBCollection dbCollection = connectToDb("iadata120125m0518",
				"InformationAsset");
		BasicDBObject query = new BasicDBObject();
		query.put("IAID", catdocref);
		DBObject doc = dbCollection.findOne(query);
		InformationAsset informationAsset = new InformationAsset();
		informationAsset.set_id(doc.get("_id").toString());
		informationAsset.setCatdocref(doc.get("IAID").toString());
		DBObject scopecontent = (BasicDBObject) doc.get("ScopeContent");
		String description = (String) scopecontent.get("Description");
		informationAsset.setDescription(description);
		return informationAsset;
	}

	public List<Category> getCategories() throws MongoException {

		List<Category> categories = new ArrayList<Category>();
		DBCollection dbCollection = connectToDb("mydb", "taxonomy");
		DBCursor cursor = dbCollection.find();
		for (DBObject doc : cursor) {
			Category category = new Category();
			category.set_id(doc.get("_id").toString());
			category.setCategory(doc.get("ttl").toString());
			category.setQUERY(doc.get("query").toString());
			categories.add(category);
		}
		cursor.close();
		return categories;
	}

	public Category getCategory(String id) throws MongoException {

		DBCollection dbCollection = connectToDb("mydb", "taxonomy");
		BasicDBObject query = new BasicDBObject();
		query.put("_id", id);
		DBObject doc = dbCollection.findOne(query);
		Category category = new Category();
		category.set_id(doc.get("_id").toString());
		category.setCategory(doc.get("CATEGORY").toString());
		category.setQUERY(doc.get("QUERY").toString());
		return category;
	}

	public void addTrainingDocument(TrainingDocument trainingDocument,
			DBCollection dBCollection) {
		BasicDBObject doc = new BasicDBObject();
		doc.put("CATEGORY", trainingDocument.getCategory());
		doc.put("DESCRIPTION",
				trainingDocument.getDescription().replaceAll("\\<.*?>", ""));
		doc.put("TITLE", trainingDocument.getTitle().replaceAll("\\<.*?>", ""));
		dBCollection.insert(doc);
	}
}

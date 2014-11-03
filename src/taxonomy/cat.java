package taxonomy;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class cat {

	public static void main(String[] args) throws IOException, ParseException {

		createIndex();

//		createTrainingSet(100);

//		createTrainingIndex();

//		categoriseDocument("FO 371/123920");

//		categorise();
	}

	private static void categorise() throws IOException {

		MongoAccess mongoAccess = new MongoAccess();

		DBCollection dbCollection = mongoAccess.connectToDb(
				"iadata120125m0518", "InformationAsset");
		DBCursor cursor = dbCollection.find();

		for (DBObject doc : cursor) {
			categoriseDocument(doc.get("IAID").toString());
		}

		System.out.println("Categorisation finished");

	}

	private static void createIndex() throws IOException {

		Indexer indexer = new Indexer();
		indexer.buildIndex();
		System.out.println("Finished building index.");
	}

	private static void categoriseDocument(String catdocref) throws IOException {

		MongoAccess mongoAccess = new MongoAccess();

		InformationAsset informationAsset = mongoAccess
				.getInformationAsset(catdocref);

		Categoriser categoriser = new Categoriser();
		Reader reader = new StringReader(informationAsset.getDescription());
		List<String> result = categoriser.runMlt("C:/TrainingIndex", reader,
				100);

		System.out.println("DOCUMENT");
		System.out.println("------------------------");
		System.out.println("TITLE: " + informationAsset.getTitle());
		System.out.println("IAID: " + informationAsset.getCatdocref());
		System.out.println("DESCRIPTION: " + informationAsset.getDescription());
		System.out.println();
		for (String category : result) {
			System.out.println("CATEGORY: " + category);
		}
		System.out.println("------------------------");

		System.out.println();

	}

	private static void createTrainingIndex() throws IOException {
		Indexer indexer = new Indexer();
		indexer.buildTrainingIndex();
		System.out.println("Finished building training index.");
	}

	private static void createTrainingSet(int trainingSetSize)
			throws IOException, ParseException {

		MongoAccess mongoAccess = new MongoAccess();
		List<Category> categories = mongoAccess.getCategories();
		Searcher searcher = new Searcher();
		for (Category category : categories) {
			List<InformationAssetView> trainingSet = searcher.performSearch(
					category.getQUERY(), trainingSetSize);
			if (trainingSet.size() > 0) {
				DBCollection dBCollection = mongoAccess.connectToDb(
						"trainingset", "trainingdocuments");
				for (InformationAssetView iaView : trainingSet) {
					TrainingDocument trainingDocument = new TrainingDocument();
					trainingDocument.setCategory(category.getCATEGORY());
					trainingDocument.setDescription(iaView.getDESCRIPTION());
					trainingDocument.setTitle(iaView.getTITLE());
					mongoAccess.addTrainingDocument(trainingDocument,
							dBCollection);
					System.out.println(trainingDocument.getCategory()
							+ ":"
							+ trainingDocument.getTitle().replaceAll("\\<.*?>",
									""));
				}
			}
		}
	}

}

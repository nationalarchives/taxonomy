package taxonomy;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.Version;

import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class cat {

	// TODO 3 add logging system to improve performances, do not use system.out!
	// TODO 0 0 deadlines, needs, will there be some time to complete things afterwards, can we provide a rough version for testing? Define an agile sprint? Write stories to start?
	// TODO 0 a Analyse the need in terms of user interface: what to put in the REST WS?
	// TODO 0 b set a local git repository
	// TODO 0 c create unit test cases to answer those needs and prepare the refactoring
	// TODO 0 d order classes into packages, then Adapt this to a Controller Service Dao architecture with interfaces (to ease the reading and use of this app)
	// TODO 1 do not use local index and mongo db, but use dev platform
	// TODO 3 handle lack of results: many NPE
	// TODO 3 empty the collections and index before repopulating them
	// TODO 3 handle concurrency issues: while creating the training set, if collection is browsed from the solr admin GUI, it crashes
	// TODO 0 a decide where to store temporary training set and new index
	
	public static void main(String[] args) throws IOException, ParseException {
		System.out.println("Start cat application.");

		// createIndex();

		// createTrainingSet(100);

//		 createTrainingIndex();

//		 categoriseIAMongoDocument("FO 371/123920");
//		categoriseIAViewSolrDocument("CO 1009/787");

		// categorise();
		
		categoriseIAViewsFromSolr();
		System.out.println("Stop cat application.");
	}

	/**
	 * Create index of IAViews from mongodb collection InformationAsset</br> NOT
	 * TO USE ATM: taken from Paul
	 * 
	 * @throws IOException
	 */
	private static void createIndex() throws IOException {

		Indexer indexer = new Indexer();
		indexer.buildIndex();
		System.out.println("Finished building index.");
	}

	private static void createTrainingIndex() throws IOException {
		System.out.println(".createTrainingIndex : START");
		Indexer indexer = new Indexer();
		indexer.buildTrainingIndex();
		System.out.println(".createTrainingIndex : END");
	}

	private static void createTrainingSet(int trainingSetSize)
			throws IOException, ParseException {
		System.out.println(".createTrainingSet : START");

		MongoAccess mongoAccess = new MongoAccess();
		List<Category> categories = mongoAccess.getCategories();
		Searcher searcher = new Searcher();
		for (Category category : categories) {
			List<InformationAssetView> trainingSet;
			try {
				trainingSet = searcher.performSearch(category.getQUERY(),
						trainingSetSize);
				System.out.println(".createTrainingSet: Category="
						+ category.getCATEGORY() + ", found "
						+ trainingSet.size() + " result(s)");
				if (trainingSet.size() > 0) {
					DBCollection dBCollection = mongoAccess.connectToDb(
							"trainingset", "InformationAssetView");
					for (InformationAssetView iaView : trainingSet) {
						TrainingDocument trainingDocument = new TrainingDocument();
						trainingDocument.setCategory(category.getCATEGORY());
						trainingDocument
								.setDescription(iaView.getDESCRIPTION());
						trainingDocument.setTitle(iaView.getTITLE());
						mongoAccess.addTrainingDocument(trainingDocument,
								dBCollection);
						System.out.println(trainingDocument.getCategory()
								+ ":"
								+ trainingDocument.getTitle().replaceAll(
										"\\<.*?>", ""));
					}
				}
			} catch (ParseException e) {
				//TODO 1 several errors occur while creating the training set, to investigate
				System.out
						.println("[ERROR] .createTrainingSet< An error occured: "
								+ e.getMessage());
			}
		}
		System.out.println(".createTrainingSet : END");
	}

	/**
	 * categorise a document by running the MLT process against the training set
	 * 
	 * @param catdocref
	 *            IAID
	 * @throws IOException
	 */
	private static void categoriseIAMongoDocument(String catdocref)
			throws IOException {

		MongoAccess mongoAccess = new MongoAccess();

		InformationAsset informationAsset = mongoAccess
				.getInformationAsset(catdocref);

		Categoriser categoriser = new Categoriser();
		Reader reader = new StringReader(informationAsset.getDescription());
		List<String> result = categoriser.runMlt(CatConstants.TRAINING_INDEX,
				reader, 100);

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

	/**
	 * categorise a document by running the MLT process against the training set
	 * 
	 * @param catdocref
	 *            IAID
	 * @throws IOException
	 * @throws ParseException 
	 */
	private static void categoriseIAViewSolrDocument(String catdocref)
			throws IOException, ParseException {
		// TODO 2 do not instantiate several indexReaders for the same index
		Indexer indexer = new Indexer();
		IndexReader indexReader = indexer
				.getIndexReader(CatConstants.IAVIEW_INDEX);
		// TODO 4 CATDOCREF in schema.xml should be stored as string?
		// and not text_gen: do not need to be tokenized. makes search by
		// catdocref more complicated that it needs (look for a bunch of terms,
		// what if they are provided in the wrong order? have to check it also
		TopDocs results = searchIAViewIndexByFieldAndPhrase("CATDOCREF",
				catdocref, 1);

		Document doc = indexReader.document(results.scoreDocs[0].doc);

		Categoriser categoriser = new Categoriser();
		Reader reader = new StringReader(doc.get("DESCRIPTION"));
		List<String> result = categoriser.runMlt(CatConstants.TRAINING_INDEX,
				reader, 100);

		System.out.println("DOCUMENT");
		System.out.println("------------------------");
		System.out.println("TITLE: " + doc.get("TITLE"));
		System.out.println("IAID: " + doc.get("CATDOCREF"));
		System.out.println("DESCRIPTION: " + doc.get("DESCRIPTION"));
		System.out.println();
		for (String category : result) {
			System.out.println("CATEGORY: " + category);
		}
		System.out.println("------------------------");

		System.out.println();

	}

	private static TopDocs searchIAViewIndexByFieldAndPhrase(String field,
			String value, int numHits) throws IOException, ParseException {
		Indexer indexer = new Indexer();
		IndexReader indexReader = indexer
				.getIndexReader(CatConstants.IAVIEW_INDEX);
		IndexSearcher searcher = new IndexSearcher(indexReader);

		QueryParser qp = new QueryParser(Version.LUCENE_44, field,new WhitespaceAnalyzer(Version.LUCENE_44));
		return searcher.search(qp.parse(QueryParser.escape(value)), numHits);
	}

	/**
	 * Categorise the whole IA collection
	 * 
	 * @throws IOException
	 */
	private static void categoriseIAViewsFromSolr() throws IOException {
		
		//TODO 1 insert results in a new database/index
		
		Indexer indexer = new Indexer();
		IndexReader indexReader = indexer.getIndexReader(CatConstants.IAVIEW_INDEX);
		for (int i = 0; i < indexReader.maxDoc(); i++) {
			//TODO 2 Add concurrency: categorize several documents at the same time
			if (indexReader.hasDeletions()) {
				System.out
						.println("[ERROR].categoriseDocument: the reader provides deleted elements though it should not");
			}

			Document doc = indexReader.document(i);

			Categoriser categoriser = new Categoriser();
			Reader reader = new StringReader(doc.get("DESCRIPTION"));
			List<String> result = categoriser.runMlt(
					CatConstants.TRAINING_INDEX, reader, 100);

			System.out.println("DOCUMENT");
			System.out.println("------------------------");
			System.out.println("TITLE: " + doc.get("TITLE"));
			System.out.println("IAID: " + doc.get("CATDOCREF"));
			System.out.println("DESCRIPTION: " + doc.get("DESCRIPTION"));
			System.out.println();
			for (String category : result) {
				System.out.println("CATEGORY: " + category);
			}
			System.out.println("------------------------");

			System.out.println();

		}

		System.out.println("Categorisation finished");

	}

	/**
	 * Categorise the whole IA collection
	 * 
	 * @throws IOException
	 */
	private static void categoriseIAsFromMongoDb() throws IOException {

		MongoAccess mongoAccess = new MongoAccess();

		DBCollection dbCollection = mongoAccess.connectToDb(
				"iadata120125m0518", "InformationAsset");
		DBCursor cursor = dbCollection.find();

		for (DBObject doc : cursor) {
			categoriseIAMongoDocument(doc.get("IAID").toString());
		}

		System.out.println("Categorisation finished");

	}

}

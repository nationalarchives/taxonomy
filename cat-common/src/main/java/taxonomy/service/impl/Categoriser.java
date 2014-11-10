package taxonomy.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queries.mlt.MoreLikeThis;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import taxonomy.CatConstants;
import taxonomy.repository.domain.Category;
import taxonomy.repository.domain.InformationAsset;
import taxonomy.repository.domain.InformationAssetView;
import taxonomy.repository.domain.TrainingDocument;
import taxonomy.repository.lucene.Indexer;
import taxonomy.repository.lucene.Searcher;
import taxonomy.repository.mongo.MongoAccess;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * class dedicated to the categorisation of documents<br/>
 * use the More Like This feature of Lucene
 *
 */
public class Categoriser {
	
	/**
	 * Create index of IAViews from mongodb collection InformationAsset</br> NOT
	 * TO USE ATM: taken from Paul
	 * 
	 * @throws IOException
	 */
	@Deprecated
	public static void createIndex() throws IOException {

		Indexer indexer = new Indexer();
		indexer.buildIndex();
		System.out.println("Finished building index.");
	}

	public static void createTrainingSet(int trainingSetSize)
			throws IOException, ParseException {
		System.out.println(".createTrainingSet : START");

		MongoAccess mongoAccess = new MongoAccess();
		List<Category> categories = mongoAccess.getCategories();
		Searcher searcher = new Searcher();
		DBCollection dBCollection = mongoAccess.getMongoCollection(
				"taxonomy", "trainingset");
		
		//empty collection
		dBCollection.remove(new BasicDBObject());
		
		
		for (Category category : categories) {
			List<InformationAssetView> trainingSet;
			try {
				trainingSet = searcher.performSearch(category.getQUERY(),
						trainingSetSize);
				System.out.println(".createTrainingSet: Category="
						+ category.getCATEGORY() + ", found "
						+ trainingSet.size() + " result(s)");
				if (trainingSet.size() > 0) {
					
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

	public static void indexTrainingSet() throws IOException {
		System.out.println(".createTrainingIndex : START");
		Indexer indexer = new Indexer();
		indexer.buildTrainingIndex();
		System.out.println(".createTrainingIndex : END");
	}

	/**
	 * categorise a document by running the MLT process against the training set
	 * 
	 * @param catdocref
	 *            IAID
	 * @throws IOException
	 */
	@Deprecated
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
	public static List<String> categoriseIAViewSolrDocument(String catdocref)
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
		
		return result;

	}

	private static TopDocs searchIAViewIndexByFieldAndPhrase(String field,
			String value, int numHits) throws IOException, ParseException {
		Indexer indexer = new Indexer();
		IndexReader indexReader = indexer
				.getIndexReader(CatConstants.IAVIEW_INDEX);
		IndexSearcher searcher = new IndexSearcher(indexReader);

		QueryParser qp = new QueryParser(CatConstants.LUCENE_VERSION, field,new WhitespaceAnalyzer(CatConstants.LUCENE_VERSION));
		return searcher.search(qp.parse(QueryParser.escape(value)), numHits);
	}

	/**
	 * Categorise the whole IA collection
	 * 
	 * @throws IOException
	 */
	public static void categoriseIAViewsFromSolr() throws IOException {
		
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
	@Deprecated
	private static void categoriseIAsFromMongoDb() throws IOException {

		MongoAccess mongoAccess = new MongoAccess();

		DBCollection dbCollection = mongoAccess.getMongoCollection(
				"iadata120125m0518", "InformationAsset");
		DBCursor cursor = dbCollection.find();

		for (DBObject doc : cursor) {
			categoriseIAMongoDocument(doc.get("IAID").toString());
		}

		System.out.println("Categorisation finished");

	}
	
	
	

	/**
	 * run More Like This process on a document by comparing its description to the description of all items of the training set<br/>
	 * currently we get a fixed number of the top results
	 * @param modelPath
	 * path of the training set index
	 * @param reader
	 * reader of the document being tested
	 * @param maxResults
	 * max number of results to return
	 * @return
	 * @throws IOException
	 */
	// TODO 1 check and update fields that are being retrieved to create training set, used for MLT (run MLT on title, context desc and desc at least. returns results by score not from a fixed number)
	public static List<String> runMlt(String modelPath, Reader reader, int maxResults)
			throws IOException {

		Directory directory = FSDirectory.open(new File(modelPath));

		DirectoryReader ireader = DirectoryReader.open(directory);
		IndexSearcher isearcher = new IndexSearcher(ireader);

		Analyzer analyzer = new EnglishAnalyzer(CatConstants.LUCENE_VERSION);

		MoreLikeThis moreLikeThis = new MoreLikeThis(ireader);
		moreLikeThis.setAnalyzer(analyzer);
		moreLikeThis.setFieldNames(new String[] { "description" });

		Query query = moreLikeThis.like(reader, "description");
		
		TopDocs topDocs = isearcher.search(query, maxResults);

		List<String> result = new ArrayList<String>();

		int size = 0;
		if (topDocs.totalHits <= 100) {
			size = topDocs.totalHits;
		}

		for (int i = 0; i < size; i++) {
			ScoreDoc scoreDoc = topDocs.scoreDocs[i];
			Document hitDoc = isearcher.doc(scoreDoc.doc);
			String category = hitDoc.get("category");
			result.add(category);
		}
		
		ireader.close();
		
		return new ArrayList<String>(new LinkedHashSet<String>(result));
	}
}

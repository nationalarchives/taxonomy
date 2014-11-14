package gov.tna.discovery.taxonomy.service.impl;

import gov.tna.discovery.taxonomy.CatConstants;
import gov.tna.discovery.taxonomy.repository.domain.InformationAssetView;
import gov.tna.discovery.taxonomy.repository.domain.InformationAssetViewFields;
import gov.tna.discovery.taxonomy.repository.domain.mongo.Category;
import gov.tna.discovery.taxonomy.repository.domain.mongo.TrainingDocument;
import gov.tna.discovery.taxonomy.repository.lucene.Indexer;
import gov.tna.discovery.taxonomy.repository.lucene.Searcher;
import gov.tna.discovery.taxonomy.repository.mongo.CategoryRepository;
import gov.tna.discovery.taxonomy.repository.mongo.TrainingDocumentRepository;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * class dedicated to the categorisation of documents<br/>
 * use the More Like This feature of Lucene
 *
 */
@Service
public class Categoriser {

    private static final Logger logger = LoggerFactory.getLogger(Indexer.class);

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    TrainingDocumentRepository trainingDocumentRepository;
    
    @Autowired
    Indexer indexer;

    /**
     * Create index of IAViews from mongodb collection InformationAsset</br> NOT
     * TO USE ATM: taken from Paul
     * 
     * @throws IOException
     */
//    @Deprecated
//    public void createIndex() throws IOException {
//
//	Indexer indexer = new Indexer();
//	indexer.buildIndex();
//	logger.debug("Finished building index.");
//    }

    public void createTrainingSet(int trainingSetSize) throws IOException, ParseException {
	logger.debug(".createTrainingSet : START");

	Iterator<Category> categoryIterator = categoryRepository.findAll().iterator();
	Searcher searcher = new Searcher();

	// empty collection
	trainingDocumentRepository.deleteAll();

	while(categoryIterator.hasNext()){
	    Category category = categoryIterator.next();
	    List<InformationAssetView> trainingSet;
	    try {
		// FIXME JCT add score
		trainingSet = searcher.performSearch(category.getQry(), null, trainingSetSize, null);
		logger.debug(".createTrainingSet: Category=" + category.getQry() + ", found " + trainingSet.size()
			+ " result(s)");
		if (trainingSet.size() > 0) {

		    for (InformationAssetView iaView : trainingSet) {
			TrainingDocument trainingDocument = new TrainingDocument();
			trainingDocument.setCATEGORY(category.getQry());
			trainingDocument.setDESCRIPTION(iaView.getDESCRIPTION());
			trainingDocument.setTITLE(iaView.getTITLE());
			trainingDocumentRepository.save(trainingDocument);
			logger.debug(trainingDocument.getCATEGORY() + ":"
				+ trainingDocument.getTITLE().replaceAll("\\<.*?>", ""));
		    }
		}
	    } catch (ParseException e) {
		// TODO 1 several errors occur while creating the training set,
		// to investigate
		logger.debug("[ERROR] .createTrainingSet< An error occured for category: " + category.toString());
		logger.debug("[ERROR] .createTrainingSet< Error message: " + e.getMessage());
	    }
	}
	logger.debug(".createTrainingSet : END");
    }

    public void indexTrainingSet() throws IOException {
	logger.debug(".createTrainingIndex : START");
	indexer.buildTrainingIndex();
	logger.debug(".createTrainingIndex : END");
    }

    /**
     * categorise a document by running the MLT process against the training set
     * 
     * @param catdocref
     *            IAID
     * @throws IOException
     */
//    @Deprecated
//    private void categoriseIAMongoDocument(String catdocref) throws IOException {
//
//	MongoInterface mongoAccess = new MongoAccess();
//
//	InformationAsset informationAsset = mongoAccess.getInformationAsset(catdocref);
//
//	Categoriser categoriser = new Categoriser();
//	Reader reader = new StringReader(informationAsset.getDescription());
//	List<String> result = categoriser.runMlt(CatConstants.TRAINING_INDEX, reader, 100);
//
//	logger.debug("DOCUMENT");
//	logger.debug("------------------------");
//	logger.debug("TITLE: " + informationAsset.getTitle());
//	logger.debug("IAID: " + informationAsset.getCatdocref());
//	logger.debug("DESCRIPTION: " + informationAsset.getDescription());
//	logger.debug("");
//	for (String category : result) {
//	    logger.debug("CATEGORY: " + category);
//	}
//	logger.debug("------------------------");
//
//	logger.debug("");
//
//    }

    /**
     * categorise a document by running the MLT process against the training set
     * 
     * @param catdocref
     *            IAID
     * @throws IOException
     * @throws ParseException
     */
    public List<String> categoriseIAViewSolrDocument(String catdocref) throws IOException, ParseException {
	// TODO 2 do not instantiate several indexReaders for the same index
	Indexer indexer = new Indexer();
	IndexReader indexReader = indexer.getIndexReader(CatConstants.IAVIEW_INDEX);
	// TODO 4 CATDOCREF in schema.xml should be stored as string?
	// and not text_gen: do not need to be tokenized. makes search by
	// catdocref more complicated that it needs (look for a bunch of terms,
	// what if they are provided in the wrong order? have to check it also
	TopDocs results = searchIAViewIndexByFieldAndPhrase("CATDOCREF", catdocref, 1);

	Document doc = indexReader.document(results.scoreDocs[0].doc);

	Categoriser categoriser = new Categoriser();
	Reader reader = new StringReader(doc.get(InformationAssetViewFields.DESCRIPTION.toString()));
	List<String> result = categoriser.runMlt(CatConstants.TRAINING_INDEX, reader, 100);

	logger.debug("DOCUMENT");
	logger.debug("------------------------");
	logger.debug("TITLE: " + doc.get("TITLE"));
	logger.debug("IAID: " + doc.get("CATDOCREF"));
	logger.debug("DESCRIPTION: " + doc.get("DESCRIPTION"));
	logger.debug("");
	for (String category : result) {
	    logger.debug("CATEGORY: " + category);
	}
	logger.debug("------------------------");

	logger.debug("");

	return result;

    }

    private TopDocs searchIAViewIndexByFieldAndPhrase(String field, String value, int numHits) throws IOException,
	    ParseException {
	Indexer indexer = new Indexer();
	IndexReader indexReader = indexer.getIndexReader(CatConstants.IAVIEW_INDEX);
	IndexSearcher searcher = new IndexSearcher(indexReader);

	QueryParser qp = new QueryParser(CatConstants.LUCENE_VERSION, field, new WhitespaceAnalyzer(
		CatConstants.LUCENE_VERSION));
	return searcher.search(qp.parse(QueryParser.escape(value)), numHits);
    }

    /**
     * Categorise the whole IA collection
     * 
     * @throws IOException
     */
    public void categoriseIAViewsFromSolr() throws IOException {

	// TODO 1 insert results in a new database/index

	Indexer indexer = new Indexer();
	IndexReader indexReader = indexer.getIndexReader(CatConstants.IAVIEW_INDEX);
	for (int i = 0; i < indexReader.maxDoc(); i++) {
	    // TODO 2 Add concurrency: categorize several documents at the same
	    // time
	    if (indexReader.hasDeletions()) {
		System.out
			.println("[ERROR].categoriseDocument: the reader provides deleted elements though it should not");
	    }

	    Document doc = indexReader.document(i);

	    Categoriser categoriser = new Categoriser();
	    Reader reader = new StringReader(doc.get(InformationAssetViewFields.DESCRIPTION.toString()));
	    List<String> result = categoriser.runMlt(CatConstants.TRAINING_INDEX, reader, 100);

	    logger.debug("DOCUMENT");
	    logger.debug("------------------------");
	    logger.debug("TITLE: " + doc.get("TITLE"));
	    logger.debug("IAID: " + doc.get("CATDOCREF"));
	    logger.debug("DESCRIPTION: " + doc.get("DESCRIPTION"));
	    logger.debug("");
	    for (String category : result) {
		logger.debug("CATEGORY: " + category);
	    }
	    logger.debug("------------------------");

	    logger.debug("");

	}

	logger.debug("Categorisation finished");

    }

    /**
     * Categorise the whole IA collection
     * 
     * @throws IOException
     */
//    @Deprecated
//    private void categoriseIAsFromMongoDb() throws IOException {
//
//	MongoAccess mongoAccess = new MongoAccess();
//
//	DBCollection dbCollection = mongoAccess
//		.getMongoCollection(CatConstants.MONGO_IA_DB, CatConstants.MONGO_IA_COLL);
//	DBCursor cursor = dbCollection.find();
//
//	for (DBObject doc : cursor) {
//	    categoriseIAMongoDocument(doc.get(InformationAssetFields.IAID.toString()).toString());
//	}
//
//	logger.debug("Categorisation finished");
//
//    }

    /**
     * run More Like This process on a document by comparing its description to
     * the description of all items of the training set<br/>
     * currently we get a fixed number of the top results
     * 
     * @param modelPath
     *            path of the training set index
     * @param reader
     *            reader of the document being tested
     * @param maxResults
     *            max number of results to return
     * @return
     * @throws IOException
     */
    // TODO 1 check and update fields that are being retrieved to create
    // training set, used for MLT (run MLT on title, context desc and desc at
    // least. returns results by score not from a fixed number)
    public List<String> runMlt(String modelPath, Reader reader, int maxResults) throws IOException {

	Directory directory = FSDirectory.open(new File(modelPath));

	DirectoryReader ireader = DirectoryReader.open(directory);
	IndexSearcher isearcher = new IndexSearcher(ireader);

	Analyzer analyzer = new EnglishAnalyzer(CatConstants.LUCENE_VERSION);

	MoreLikeThis moreLikeThis = new MoreLikeThis(ireader);
	moreLikeThis.setAnalyzer(analyzer);
	moreLikeThis.setFieldNames(new String[] { InformationAssetViewFields.DESCRIPTION.toString() });

	Query query = moreLikeThis.like(reader, InformationAssetViewFields.DESCRIPTION.toString());

	TopDocs topDocs = isearcher.search(query, maxResults);

	List<String> result = new ArrayList<String>();

	int size = 0;
	if (topDocs.totalHits <= 100) {
	    size = topDocs.totalHits;
	}

	for (int i = 0; i < size; i++) {
	    ScoreDoc scoreDoc = topDocs.scoreDocs[i];
	    Document hitDoc = isearcher.doc(scoreDoc.doc);
	    String category = hitDoc.get(InformationAssetViewFields.CATEGORY.toString());
	    result.add(category);
	}

	ireader.close();

	return new ArrayList<String>(new LinkedHashSet<String>(result));
    }
}

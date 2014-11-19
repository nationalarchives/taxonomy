package gov.tna.discovery.taxonomy.service.impl;

import gov.tna.discovery.taxonomy.CatConstants;
import gov.tna.discovery.taxonomy.repository.domain.TrainingDocument;
import gov.tna.discovery.taxonomy.repository.domain.lucene.InformationAssetView;
import gov.tna.discovery.taxonomy.repository.domain.lucene.InformationAssetViewFields;
import gov.tna.discovery.taxonomy.repository.domain.mongo.Category;
import gov.tna.discovery.taxonomy.repository.lucene.Indexer;
import gov.tna.discovery.taxonomy.repository.lucene.Searcher;
import gov.tna.discovery.taxonomy.repository.mongo.CategoryRepository;
import gov.tna.discovery.taxonomy.repository.mongo.TrainingDocumentRepository;
import gov.tna.discovery.taxonomy.service.exception.TaxonomyErrorType;
import gov.tna.discovery.taxonomy.service.exception.TaxonomyException;

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

    private static final Logger logger = LoggerFactory.getLogger(Categoriser.class);

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    TrainingDocumentRepository trainingDocumentRepository;

    @Autowired
    Indexer indexer;

    @Autowired
    TrainingSetService trainingSetService;

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
	logger.debug("TITLE: {}", doc.get("TITLE"));
	logger.debug("IAID: {}", doc.get("CATDOCREF"));
	logger.debug("DESCRIPTION: {}", doc.get("DESCRIPTION"));
	logger.debug("");
	for (String category : result) {
	    logger.debug("CATEGORY: {}", category);
	}
	logger.debug("------------------------");

	logger.debug("");

	return result;

    }

    private TopDocs searchIAViewIndexByFieldAndPhrase(String field, String value, int numHits) throws IOException,
	    ParseException {
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
	    logger.debug("TITLE: {}", doc.get("TITLE"));
	    logger.debug("IAID: {}", doc.get("CATDOCREF"));
	    logger.debug("DESCRIPTION: {}", doc.get("DESCRIPTION"));
	    logger.debug("");
	    for (String category : result) {
		logger.info("Document {} has CATEGORY: {}", doc.get("CATDOCREF"), category);
	    }
	    logger.debug("------------------------");

	    logger.debug("");

	}

	logger.debug("Categorisation finished");

    }

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

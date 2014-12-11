package gov.tna.discovery.taxonomy.common.service.impl;

import gov.tna.discovery.taxonomy.common.repository.domain.lucene.InformationAssetView;
import gov.tna.discovery.taxonomy.common.repository.domain.lucene.InformationAssetViewFields;
import gov.tna.discovery.taxonomy.common.repository.lucene.IAViewRepository;
import gov.tna.discovery.taxonomy.common.repository.lucene.LuceneHelperTools;
import gov.tna.discovery.taxonomy.common.service.CategoriserService;
import gov.tna.discovery.taxonomy.common.service.domain.CategorisationResult;
import gov.tna.discovery.taxonomy.common.service.exception.TaxonomyErrorType;
import gov.tna.discovery.taxonomy.common.service.exception.TaxonomyException;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queries.mlt.MoreLikeThis;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * class dedicated to the categorisation of documents<br/>
 * use the More Like This feature of Lucene
 *
 */
@Service
public class CategoriserServiceImpl implements CategoriserService {

    private static final Logger logger = LoggerFactory.getLogger(CategoriserServiceImpl.class);

    @Autowired
    private IndexReader iaViewIndexReader;

    @Autowired
    private IndexReader trainingSetIndexReader;

    @Autowired
    private IAViewRepository iaViewRepository;

    @Autowired
    private SearcherManager trainingSetSearcherManager;

    @Autowired
    private Analyzer trainingSetAnalyser;

    @Value("${lucene.mlt.mimimumScoreForMlt}")
    private float mimimumScoreForMlt;
    @Value("${lucene.mlt.mimimumGlobalScoreForACategory}")
    private float mimimumGlobalScoreForACategory;
    @Value("${lucene.mlt.maximumSimilarElements}")
    private int maximumSimilarElements;

    @Value("${lucene.mlt.minTermFreq}")
    private int minTermFreq;

    @Value("${lucene.mlt.minDocFreq}")
    private int minDocFreq;

    /*
     * (non-Javadoc)
     * 
     * @see gov.tna.discovery.taxonomy.common.service.impl.Categoriser#
     * categoriseIAViewSolrDocument(java.lang.String)
     */
    @Override
    public List<CategorisationResult> categoriseIAViewSolrDocument(String catdocref) {
	// TODO 4 CATDOCREF in schema.xml should be stored as string?
	// and not text_gen: do not need to be tokenized. makes search by
	// catdocref more complicated that it needs (look for a bunch of terms,
	// what if they are provided in the wrong order? have to check it also
	TopDocs results = iaViewRepository.searchIAViewIndexByFieldAndPhrase("CATDOCREF", catdocref, 1);

	Document doc;
	try {
	    doc = this.iaViewIndexReader.document(results.scoreDocs[0].doc);
	} catch (IOException e) {
	    throw new TaxonomyException(TaxonomyErrorType.LUCENE_IO_EXCEPTION, e);
	}

	List<CategorisationResult> result = runMlt(doc);

	logger.debug("DOCUMENT");
	logger.debug("------------------------");
	logger.debug("TITLE: {}", doc.get("TITLE"));
	logger.debug("IAID: {}", doc.get("CATDOCREF"));
	logger.debug("DESCRIPTION: {}", doc.get("DESCRIPTION"));
	logger.debug("");
	for (CategorisationResult categoryResult : result) {
	    logger.debug("CATEGORY: {}, score: {}, number of found documents: {}", categoryResult.getName(),
		    categoryResult.getScore(), categoryResult.getNumberOfFoundDocuments());
	}
	logger.debug("------------------------");

	logger.debug("");

	return result;

    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.tna.discovery.taxonomy.common.service.impl.Categoriser#
     * categoriseIAViewSolrIndex ()
     */
    @Override
    public void testCategoriseIAViewSolrIndex() throws IOException {

	// TODO 1 insert results in a new database/index

	for (int i = 0; i < this.iaViewIndexReader.maxDoc(); i++) {
	    // TODO 2 Add concurrency: categorize several documents at the same
	    // time
	    if (this.iaViewIndexReader.hasDeletions()) {
		logger.error(".testCategoriseIAViewSolrIndex: the reader provides deleted elements though it should not");
	    }

	    Document doc = this.iaViewIndexReader.document(i);

	    List<CategorisationResult> result = runMlt(doc);

	    logger.debug("DOCUMENT");
	    logger.debug("------------------------");
	    logger.debug("TITLE: {}", doc.get("TITLE"));
	    logger.debug("IAID: {}", doc.get("CATDOCREF"));
	    logger.debug("DESCRIPTION: {}", doc.get("DESCRIPTION"));
	    logger.debug("");
	    for (CategorisationResult categoryResult : result) {
		logger.info("CATEGORY: {}, score: {}, number of found documents: {}", categoryResult.getName(),
			categoryResult.getScore(), categoryResult.getNumberOfFoundDocuments());
	    }
	    logger.debug("------------------------");

	    logger.debug("");

	}

	logger.debug("Categorisation finished");

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * gov.tna.discovery.taxonomy.common.service.impl.Categoriser#runMlt(java
     * .io.Reader )
     */
    // TODO 1 check and update fields that are being retrieved to create
    // training set, used for MLT (run MLT on title, context desc and desc at
    // least. returns results by score not from a fixed number)
    @Override
    public List<CategorisationResult> runMlt(Document document) {

	Map<String, CategorisationResult> result = null;
	IndexSearcher searcher = null;
	try {
	    trainingSetSearcherManager.maybeRefresh();
	    // Boolean wasRefreshed = trainingSetSearcherManager.maybeRefresh();
	    // if (wasRefreshed) {
	    // logger.debug(".runMlt: training set searcher had to be refreshed");
	    // }
	    searcher = trainingSetSearcherManager.acquire();

	    MoreLikeThis moreLikeThis = new MoreLikeThis(this.trainingSetIndexReader);
	    moreLikeThis.setMinTermFreq(minTermFreq);
	    moreLikeThis.setMinDocFreq(minDocFreq);
	    moreLikeThis.setAnalyzer(this.trainingSetAnalyser);
	    moreLikeThis.setFieldNames(iaViewRepository.fieldsToAnalyse);

	    BooleanQuery fullQuery = new BooleanQuery();

	    for (String fieldName : iaViewRepository.fieldsToAnalyse) {
		String value = document.get(fieldName);
		if (value != null && !"null".equals(value)) {
		    Query query = moreLikeThis.like(new StringReader(value), fieldName);
		    fullQuery.add(query, Occur.SHOULD);
		}
	    }

	    TopDocs topDocs = searcher.search(fullQuery, this.maximumSimilarElements);
	    logger.info(".runMlt: found {} total hits, processed {} hits", topDocs.totalHits,
		    this.maximumSimilarElements);

	    result = new LinkedHashMap<String, CategorisationResult>();

	    int size = 0;
	    if (topDocs.totalHits <= this.maximumSimilarElements) {
		size = topDocs.totalHits - 1;
	    } else {
		size = this.maximumSimilarElements - 1;
	    }

	    for (int i = 0; i < size; i++) {
		ScoreDoc scoreDoc = topDocs.scoreDocs[i];
		Float currrentScore = scoreDoc.score;

		if (currrentScore < this.mimimumScoreForMlt) {
		    break;
		}

		Document hitDoc = searcher.doc(scoreDoc.doc);
		String category = hitDoc.get(InformationAssetViewFields.CATEGORY.toString());
		String docReference = hitDoc.get(InformationAssetViewFields.DOCREFERENCE.toString());
		logger.debug(".runMlt: found doc, category: {}, score: {}, docreference: {}", category, currrentScore,
			docReference);

		CategorisationResult existingCategorisationResult = result.get(category);
		Float scoreToSet = currrentScore;
		Integer numberOfFoundDocuments = 1;
		// k nearest neighbour algorithm
		if (existingCategorisationResult != null) {
		    scoreToSet += existingCategorisationResult.getScore();
		    numberOfFoundDocuments += existingCategorisationResult.getNumberOfFoundDocuments();
		}
		result.put(category, new CategorisationResult(category, scoreToSet, numberOfFoundDocuments));

	    }

	} catch (IOException e) {
	    throw new TaxonomyException(TaxonomyErrorType.LUCENE_IO_EXCEPTION, e);
	} finally {
	    LuceneHelperTools.releaseSearcherManagerQuietly(trainingSetSearcherManager, searcher);
	}

	List<CategorisationResult> sortedResults = sortCategorisationResultsByScoreDescAndFilterByGlobalScore(new ArrayList<CategorisationResult>(
		result.values()));

	return sortedResults;
    }

    private List<CategorisationResult> sortCategorisationResultsByScoreDescAndFilterByGlobalScore(
	    List<CategorisationResult> categorisationResults) {
	// Sort results by Score in descending Order
	Collections.sort(categorisationResults, new Comparator<CategorisationResult>() {
	    public int compare(CategorisationResult a, CategorisationResult b) {
		return b.getScore().compareTo(a.getScore());
	    }
	});

	// add entries to the linkedList to return. Do not add entries below the
	// minimum global score for a category
	List<CategorisationResult> sortedResults = new LinkedList<CategorisationResult>();
	for (CategorisationResult entry : categorisationResults) {
	    if (entry.getScore() < this.mimimumGlobalScoreForACategory) {
		break;
	    }
	    sortedResults.add(entry);
	}
	return sortedResults;
    }

    /*
     * (non-Javadoc)
     * 
     * @see gov.tna.discovery.taxonomy.common.service.impl.Categoriser#
     * testCategoriseSingle (gov
     * .tna.discovery.taxonomy.repository.domain.lucene.InformationAssetView)
     */
    @Override
    public List<CategorisationResult> testCategoriseSingle(InformationAssetView iaView) {

	Document doc = new Document();
	try {
	    for (Field field : iaView.getClass().getDeclaredFields()) {
		field.setAccessible(true);
		String fieldName = field.getName();

		if (CollectionUtils.contains(Arrays.asList(iaViewRepository.fieldsToAnalyse).iterator(), fieldName)) {
		    String value = String.valueOf(field.get(iaView));
		    if (value != null && !"null".equals(value)) {
			doc.add(new TextField(fieldName, value, Store.YES));
		    }
		}
	    }

	} catch (IllegalArgumentException e) {
	    logger.error(".testCategoriseSingle: unexpected error: {}", e.getMessage());
	    throw new RuntimeException(e);
	} catch (IllegalAccessException e) {
	    logger.error(".testCategoriseSingle: unexpected error: {}", e.getMessage());
	    throw new RuntimeException(e);
	}

	return runMlt(doc);
    }

}

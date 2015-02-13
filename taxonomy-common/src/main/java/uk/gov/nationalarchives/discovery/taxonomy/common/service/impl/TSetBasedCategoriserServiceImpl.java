package uk.gov.nationalarchives.discovery.taxonomy.common.service.impl;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetView;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetViewFields;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.TSetBasedCategorisationResult;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.exception.TaxonomyErrorType;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.exception.TaxonomyException;
import uk.gov.nationalarchives.discovery.taxonomy.common.mapper.LuceneTaxonomyMapper;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.IAViewRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.tools.LuceneHelperTools;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.CategoriserService;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * class dedicated to the categorisation of documents<br/>
 * use the More Like This feature of Lucene
 *
 */
@Service
@ConditionalOnProperty(prefix = "lucene.categoriser.", value = "useTSetBasedCategoriser")
public class TSetBasedCategoriserServiceImpl implements CategoriserService<TSetBasedCategorisationResult> {

    private static final Logger logger = LoggerFactory.getLogger(TSetBasedCategoriserServiceImpl.class);

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

    @Value("${lucene.mlt.descBoostingFactor}")
    private float descBoostingFactor;

    @Value("${lucene.mlt.contextDescBoostingFactor}")
    private float contextDescBoostingFactor;

    @Value("${lucene.mlt.titleBoostingFactor}")
    private float titleBoostingFactor;

    @Value("${lucene.categoriser.fieldsToAnalyse}")
    private String fieldsToAnalyse;

    /**
     * run More Like This process on a document by comparing its description to
     * the description of all items of the training set<br/>
     * currently we get a fixed number of the top results
     * 
     * @param document
     *            document being tested
     * @return
     * @throws IOException
     */
    public List<TSetBasedCategorisationResult> runMlt(Document document) {

	Map<String, TSetBasedCategorisationResult> result = null;
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
	    moreLikeThis.setFieldNames(fieldsToAnalyse.split(","));
	    moreLikeThis.setBoost(true);

	    BooleanQuery fullQuery = new BooleanQuery();

	    for (String fieldName : fieldsToAnalyse.split(",")) {
		String value = document.get(fieldName);
		if (value != null && !"null".equals(value)) {

		    switch (InformationAssetViewFields.valueOf(fieldName)) {
		    case DESCRIPTION:
			moreLikeThis.setBoostFactor(descBoostingFactor);
			break;
		    case TITLE:
			moreLikeThis.setBoostFactor(titleBoostingFactor);
			break;
		    case CONTEXTDESCRIPTION:
			moreLikeThis.setBoostFactor(contextDescBoostingFactor);
			break;
		    default:
		    case SUBJECTS:
		    case CORPBODYS:
		    case PERSON_FULLNAME:
		    case PLACE_NAME:
			moreLikeThis.setBoostFactor(1);
			break;
		    }
		    Query query = moreLikeThis.like(fieldName, new StringReader(value));
		    fullQuery.add(query, Occur.SHOULD);
		}
	    }

	    TopDocs topDocs = searcher.search(fullQuery, this.maximumSimilarElements);
	    logger.debug(".runMlt: found {} total hits, processed at maximum {} hits", topDocs.totalHits,
		    this.maximumSimilarElements);

	    result = new LinkedHashMap<String, TSetBasedCategorisationResult>();

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

		TSetBasedCategorisationResult existingCategorisationResult = result.get(category);
		Float scoreToSet = currrentScore;
		Integer numberOfFoundDocuments = 1;
		// k nearest neighbour algorithm
		if (existingCategorisationResult != null) {
		    scoreToSet += existingCategorisationResult.getScore();
		    numberOfFoundDocuments += existingCategorisationResult.getNumberOfFoundDocuments();
		}
		result.put(category, new TSetBasedCategorisationResult(category, scoreToSet, numberOfFoundDocuments));

	    }

	} catch (IOException e) {
	    throw new TaxonomyException(TaxonomyErrorType.LUCENE_IO_EXCEPTION, e);
	} finally {
	    LuceneHelperTools.releaseSearcherManagerQuietly(trainingSetSearcherManager, searcher);
	}

	List<TSetBasedCategorisationResult> sortedResults = sortCategorisationResultsByScoreDescAndFilterByGlobalScore(new ArrayList<TSetBasedCategorisationResult>(
		result.values()));

	return sortedResults;
    }

    private List<TSetBasedCategorisationResult> sortCategorisationResultsByScoreDescAndFilterByGlobalScore(
	    List<TSetBasedCategorisationResult> categorisationResults) {
	// Sort results by Score in descending Order
	Collections.sort(categorisationResults, new Comparator<TSetBasedCategorisationResult>() {
	    public int compare(TSetBasedCategorisationResult a, TSetBasedCategorisationResult b) {
		return b.getScore().compareTo(a.getScore());
	    }
	});

	// add entries to the linkedList to return. Do not add entries below the
	// minimum global score for a category
	List<TSetBasedCategorisationResult> sortedResults = new LinkedList<TSetBasedCategorisationResult>();
	for (TSetBasedCategorisationResult entry : categorisationResults) {
	    if (entry.getScore() < this.mimimumGlobalScoreForACategory) {
		break;
	    }
	    sortedResults.add(entry);
	}
	return sortedResults;
    }

    public List<TSetBasedCategorisationResult> testCategoriseSingle(InformationAssetView iaView) {

	logger.info(".testCategoriseSingle: catdocref:{}, docreference:{} ", iaView.getCATDOCREF(),
		iaView.getDOCREFERENCE());
	Document doc = new Document();
	try {
	    for (Field field : iaView.getClass().getDeclaredFields()) {
		field.setAccessible(true);
		String fieldName = field.getName();

		if (CollectionUtils.contains(Arrays.asList(fieldsToAnalyse.split(",")).iterator(), fieldName)) {
		    String value = String.valueOf(field.get(iaView));
		    if (value != null && !"null".equals(value)) {
			doc.add(new TextField(fieldName, value, Store.YES));
		    }
		}
	    }

	} catch (IllegalArgumentException e) {
	    throw new RuntimeException(e);
	} catch (IllegalAccessException e) {
	    throw new RuntimeException(e);
	}

	return runMlt(doc);
    }

    @Override
    public List<TSetBasedCategorisationResult> testCategoriseSingle(String docReference) {
	logger.info(".testCategoriseSingle: docreference:{} ", docReference);
	return testCategoriseSingle(LuceneTaxonomyMapper.getIAViewFromLuceneDocument(iaViewRepository
		.searchDocByDocReference(docReference)));
    }

    @Override
    public List<TSetBasedCategorisationResult> categoriseSingle(String docReference) {
	// TODO TSETBASED Auto-generated method stub
	return null;
    }

    public List<TSetBasedCategorisationResult> categoriseSingle(InformationAssetView iaView) {
	// TODO TSETBASED Auto-generated method stub
	return null;
    }
}

package uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import uk.gov.nationalarchives.discovery.taxonomy.common.aop.annotation.Loggable;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetView;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetViewFields;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.PaginatedList;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.exception.TaxonomyErrorType;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.exception.TaxonomyException;
import uk.gov.nationalarchives.discovery.taxonomy.common.mapper.LuceneTaxonomyMapper;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.parser.TaxonomyQueryParser;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.tools.LuceneHelperTools;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.tools.TaxonomyHelperTools;

/**
 * Repository dedicated the the retrieval, storage, search of IAViews on the
 * snapshot of the Solr Cloud index. Use Lucene to process the index directly
 * 
 * @author jcharlet
 *
 */
@Repository
public class IAViewRepository {

    private SearcherManager iaviewSearcherManager;

    @Value("${lucene.index.version}")
    private String luceneVersion;

    private final Analyzer iaViewSearchAnalyser;

    private static final Logger logger = LoggerFactory.getLogger(IAViewRepository.class);

    private final LuceneHelperTools luceneHelperTools;

    @Autowired
    public IAViewRepository(SearcherManager iaviewSearcherManager, Analyzer iaViewSearchAnalyser,
	    LuceneHelperTools luceneHelperTools) {
	super();
	this.iaviewSearcherManager = iaviewSearcherManager;
	this.iaViewSearchAnalyser = iaViewSearchAnalyser;
	this.luceneHelperTools = luceneHelperTools;
    }

    public Document getDoc(ScoreDoc scoreDoc) {
	Document hitDoc = null;
	IndexSearcher searcher = null;
	try {
	    searcher = iaviewSearcherManager.acquire();
	    hitDoc = searcher.doc(scoreDoc.doc);
	} catch (IOException e) {
	    throw new TaxonomyException(TaxonomyErrorType.LUCENE_IO_EXCEPTION, e);
	} finally {
	    LuceneHelperTools.releaseSearcherManagerQuietly(iaviewSearcherManager, searcher);
	}
	return hitDoc;
    }

    /**
     * get doc by DocReference (unique field of IAView)
     * 
     * @param docReference
     * @return
     */
    public Document searchDocByDocReference(String docReference) {
	Document hitDoc = null;
	IndexSearcher searcher = null;
	try {
	    searcher = iaviewSearcherManager.acquire();
	    TopDocs results = searcher.search(new TermQuery(new Term(
		    InformationAssetViewFields.DOCREFERENCE.toString(), docReference)), 1);
	    if (results.totalHits != 1) {
		throw new TaxonomyException(TaxonomyErrorType.INVALID_PARAMETER, "searchDocByDocReference: there were "
			+ results.totalHits + " results for DOCREFERENCE: " + docReference
			+ " though it should have found only 1 doc");
	    }
	    hitDoc = searcher.doc(results.scoreDocs[0].doc);

	} catch (IOException e) {
	    throw new TaxonomyException(TaxonomyErrorType.LUCENE_IO_EXCEPTION, e);
	} finally {
	    LuceneHelperTools.releaseSearcherManagerQuietly(iaviewSearcherManager, searcher);
	}
	return hitDoc;
    }

    @Loggable
    public TopDocs performSearchWithoutAnyPostProcessing(String queryString, Filter filter, Double mimimumScore,
	    Integer limit, Integer offset) {

	IndexSearcher isearcher = null;
	try {
	    isearcher = iaviewSearcherManager.acquire();

	    Query finalQuery = luceneHelperTools.buildSearchQueryWithFiltersIfNecessary(queryString, filter);

	    return isearcher.search(finalQuery, offset + limit);
	    // return this.iaviewSearcher.search(finalQuery, offset + limit);

	} catch (IOException e) {
	    throw new TaxonomyException(TaxonomyErrorType.LUCENE_IO_EXCEPTION, e);
	} finally {
	    LuceneHelperTools.releaseSearcherManagerQuietly(iaviewSearcherManager, isearcher);
	}
    }

    public PaginatedList<InformationAssetView> performSearch(String queryString, Double mimimumScore, Integer limit,
	    Integer offset) {
	long startTimer = TaxonomyHelperTools.startTimer();

	PaginatedList<InformationAssetView> paginatedListOfIAViews = new PaginatedList<InformationAssetView>(limit,
		offset, mimimumScore);
	List<InformationAssetView> docs = new ArrayList<InformationAssetView>();

	IndexSearcher isearcher = null;
	try {
	    isearcher = iaviewSearcherManager.acquire();

	    Query finalQuery = luceneHelperTools.buildSearchQueryWithFiltersIfNecessary(queryString, null);

	    TopDocs topDocs = isearcher.search(finalQuery, offset + limit);
	    logger.debug(".performSearch: found {} total hits, time: {}", topDocs.totalHits,
		    TaxonomyHelperTools.getTimerDifference(startTimer));

	    if (mimimumScore != null) {
		Integer nbOfElementsAboveScore = getNbOfElementsAboveScore(mimimumScore, isearcher, finalQuery);
		paginatedListOfIAViews.setNumberOfResults(nbOfElementsAboveScore);
		logger.debug(".performSearch: found {} hits for that minimum score {}",
			paginatedListOfIAViews.getNumberOfResults(), paginatedListOfIAViews.getMinimumScore());
	    } else {
		paginatedListOfIAViews.setNumberOfResults(topDocs.totalHits);
	    }

	    int totalNumberOfDocumentsToParse = offset + limit;
	    if (topDocs.totalHits < offset) {
		paginatedListOfIAViews.setResults(docs);
		return paginatedListOfIAViews;
	    } else if (topDocs.totalHits < totalNumberOfDocumentsToParse) {
		totalNumberOfDocumentsToParse = topDocs.totalHits;
	    }
	    for (int i = offset; i < totalNumberOfDocumentsToParse; i++) {

		ScoreDoc scoreDoc = topDocs.scoreDocs[i];
		if (mimimumScore != null && (double) scoreDoc.score < mimimumScore) {
		    break;
		}
		Document hitDoc = isearcher.doc(scoreDoc.doc);
		InformationAssetView assetView = LuceneTaxonomyMapper.getIAViewFromLuceneDocument(hitDoc);
		assetView.setScore(scoreDoc.score);
		docs.add(assetView);
	    }
	} catch (IOException e) {
	    throw new TaxonomyException(TaxonomyErrorType.LUCENE_IO_EXCEPTION, e);
	} finally {
	    LuceneHelperTools.releaseSearcherManagerQuietly(iaviewSearcherManager, isearcher);
	}
	paginatedListOfIAViews.setResults(docs);

	return paginatedListOfIAViews;
    }

    // TODO TSETBASED & WS check if there are memory leak
    public Integer getNbOfElementsAboveScore(Double mimimumScore, IndexSearcher isearcher, Query query)
	    throws IOException {

	TopDocs topDocs = isearcher.search(query, 1);
	Integer totalHits = topDocs.totalHits;

	if (mimimumScore == 0 || totalHits == 0) {
	    return totalHits;
	}

	topDocs = isearcher.search(query, totalHits);
	Integer nbOfElementsAboveScore = 0;
	for (ScoreDoc searchResult : topDocs.scoreDocs) {
	    if ((double) searchResult.score >= mimimumScore) {
		nbOfElementsAboveScore++;
		continue;
	    }
	    break;
	}
	return nbOfElementsAboveScore;
    }

    public TopDocs searchIAViewIndexByFieldAndPhrase(String field, String value, int numHits) {
	IndexSearcher searcher = null;
	try {
	    searcher = iaviewSearcherManager.acquire();

	    QueryParser qp = new QueryParser(field, this.iaViewSearchAnalyser);

	    return searcher.search(qp.parse(QueryParser.escape(value)), numHits);

	} catch (IOException ioException) {
	    throw new TaxonomyException(TaxonomyErrorType.LUCENE_IO_EXCEPTION, ioException);
	} catch (ParseException parseException) {
	    throw new TaxonomyException(TaxonomyErrorType.LUCENE_PARSE_EXCEPTION, parseException);
	} finally {
	    LuceneHelperTools.releaseSearcherManagerQuietly(iaviewSearcherManager, searcher);
	}
    }

    public void checkCategoryQueryValidity(String qry) {
	QueryParser parser = new QueryParser("CATEGORY", this.iaViewSearchAnalyser);
	parser.setAllowLeadingWildcard(true);
	try {
	    parser.parse(qry);
	} catch (ParseException e) {
	    throw new TaxonomyException(TaxonomyErrorType.INVALID_CATEGORY_QUERY, e);
	}
    }

    public void setIaviewSearcherManager(SearcherManager iaviewSearcherManager) {
	this.iaviewSearcherManager = iaviewSearcherManager;
    }

    /**
     * refresh the index used for categorisation.<br/>
     * It is necessary to call that method if the document to categorise was
     * indexed right before that call
     */
    public void refreshIndexUsedForCategorisation() {
	try {
	    iaviewSearcherManager.maybeRefreshBlocking();
	} catch (IOException e) {
	    logger.error(".refreshIndexUsedForCategorisation: exception was raised when trying to refresh the lucene Index");
	    throw new TaxonomyException(TaxonomyErrorType.LUCENE_IO_EXCEPTION, e);
	}
    }
}

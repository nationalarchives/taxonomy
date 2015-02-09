package gov.tna.discovery.taxonomy.common.repository.lucene;

import gov.tna.discovery.taxonomy.common.aop.annotation.Loggable;
import gov.tna.discovery.taxonomy.common.mapper.LuceneTaxonomyMapper;
import gov.tna.discovery.taxonomy.common.repository.domain.lucene.InformationAssetView;
import gov.tna.discovery.taxonomy.common.repository.domain.lucene.InformationAssetViewFields;
import gov.tna.discovery.taxonomy.common.service.TaxonomyHelperTools;
import gov.tna.discovery.taxonomy.common.service.domain.PaginatedList;
import gov.tna.discovery.taxonomy.common.service.exception.TaxonomyErrorType;
import gov.tna.discovery.taxonomy.common.service.exception.TaxonomyException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeFilter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

//TODO put timeout on search requests on index: related to wildcard
//TODO create IAVIewService and put in IAViewRepository only simple execution of queries
@Component
public class IAViewRepository {

    @Autowired
    private SearcherManager iaviewSearcherManager;

    @Value("${lucene.index.version}")
    private String luceneVersion;

    @Autowired
    private Analyzer iaViewSearchAnalyser;

    @Autowired
    private IndexSearcher iaviewSearcher;

    @Autowired
    private Filter catalogueFilter;

    private static final Logger logger = LoggerFactory.getLogger(IAViewRepository.class);

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

	// IndexSearcher isearcher = null;
	try {
	    // isearcher = iaviewSearcherManager.acquire();

	    Query finalQuery = buildSearchQueryWithFiltersIfNecessary(queryString, filter);

	    // return isearcher.search(finalQuery, offset + limit);
	    return this.iaviewSearcher.search(finalQuery, offset + limit);

	} catch (IOException e) {
	    throw new TaxonomyException(TaxonomyErrorType.LUCENE_IO_EXCEPTION, e);
	} finally {
	    // LuceneHelperTools.releaseSearcherManagerQuietly(iaviewSearcherManager,
	    // isearcher);
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

	    Query finalQuery = buildSearchQueryWithFiltersIfNecessary(queryString, null);

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

    public Query buildSearchQueryWithFiltersIfNecessary(String queryString, Filter filter) {
	Query searchQuery = buildSearchQuery(queryString);

	if (filter == null) {
	    filter = this.catalogueFilter;
	}

	Query finalQuery;
	if (filter != null) {
	    finalQuery = new FilteredQuery(searchQuery, filter);
	} else {
	    finalQuery = searchQuery;
	}
	return finalQuery;
    }

    public Query buildSearchQuery(String queryString) {
	QueryParser parser = new QueryParser(InformationAssetViewFields.texttax.toString(), this.iaViewSearchAnalyser);
	parser.setAllowLeadingWildcard(true);
	Query searchQuery;
	try {
	    searchQuery = parser.parse(queryString);
	} catch (ParseException e) {
	    throw new TaxonomyException(TaxonomyErrorType.INVALID_CATEGORY_QUERY, e);
	}
	return searchQuery;
    }

    // FIXME pay attention to memory leak
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
}

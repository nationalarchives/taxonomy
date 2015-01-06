package gov.tna.discovery.taxonomy.common.repository.lucene;

import gov.tna.discovery.taxonomy.common.mapper.LuceneTaxonomyMapper;
import gov.tna.discovery.taxonomy.common.repository.domain.lucene.InformationAssetView;
import gov.tna.discovery.taxonomy.common.service.domain.PaginatedList;
import gov.tna.discovery.taxonomy.common.service.exception.TaxonomyErrorType;
import gov.tna.discovery.taxonomy.common.service.exception.TaxonomyException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
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
import org.springframework.stereotype.Component;

//TODO put timeout on search requests on index: related to wildcard
//TODO create IAVIewService and put in IAViewRepository only simple execution of queries
@Component
public class IAViewRepository {

    @Autowired
    private SearcherManager iaviewSearcherManager;

    @Value("${lucene.index.version}")
    private String luceneVersion;

    @Value("${lucene.categoriser.fieldsToAnalyse}")
    private String fieldsToAnalyse;

    @Autowired
    private Analyzer categoryQueryAnalyser;

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

    public PaginatedList<InformationAssetView> performSearch(String queryString, Double mimimumScore, Integer limit,
	    Integer offset) {
	PaginatedList<InformationAssetView> paginatedListOfIAViews = new PaginatedList<InformationAssetView>(limit,
		offset, mimimumScore);
	List<InformationAssetView> docs = new ArrayList<InformationAssetView>();

	IndexSearcher isearcher = null;
	try {
	    isearcher = iaviewSearcherManager.acquire();

	    QueryParser parser = new MultiFieldQueryParser(Version.valueOf(luceneVersion), fieldsToAnalyse.split(","),
		    this.categoryQueryAnalyser);
	    parser.setAllowLeadingWildcard(true);
	    Query query;
	    try {
		query = parser.parse(queryString);
	    } catch (ParseException e) {
		throw new TaxonomyException(TaxonomyErrorType.INVALID_CATEGORY_QUERY, e);
	    }

	    TopDocs topDocs = isearcher.search(query, offset + limit);
	    logger.debug(".performSearch: found {} total hits", topDocs.totalHits);

	    if (mimimumScore != null) {
		Integer nbOfElementsAboveScore = getNbOfElementsAboveScore(mimimumScore, isearcher, query);
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

	    QueryParser qp = new QueryParser(Version.valueOf(luceneVersion), field, this.categoryQueryAnalyser);

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
	QueryParser parser = new QueryParser(Version.valueOf(luceneVersion), "CATEGORY", this.categoryQueryAnalyser);
	parser.setAllowLeadingWildcard(true);
	try {
	    parser.parse(qry);
	} catch (ParseException e) {
	    throw new TaxonomyException(TaxonomyErrorType.INVALID_CATEGORY_QUERY, e);
	}
    }
}

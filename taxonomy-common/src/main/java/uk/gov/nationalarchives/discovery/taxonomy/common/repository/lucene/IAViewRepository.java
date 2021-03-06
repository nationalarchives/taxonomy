/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.SortField.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.annotation.Loggable;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.exception.TaxonomyErrorType;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.exception.TaxonomyException;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.BrowseAllDocsResponse;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetView;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetViewFields;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.PaginatedList;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.tools.LuceneHelperTools;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.tools.LuceneTaxonomyMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

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
    public InformationAssetView searchDocByDocReference(String docReference) {
	Document hitDoc = null;
	IndexSearcher searcher = null;
	try {
	    searcher = iaviewSearcherManager.acquire();
	    TopDocs results = searcher.search(new TermQuery(new Term(
		    InformationAssetViewFields.DOCREFERENCE.toString(), docReference)), 1);
	    if (results.totalHits != 1) {
		throw new TaxonomyException(TaxonomyErrorType.DOC_NOT_FOUND, "searchDocByDocReference: there were "
			+ results.totalHits + " results for DOCREFERENCE: " + docReference
			+ " though it should have found 1 doc");
	    }
	    hitDoc = searcher.doc(results.scoreDocs[0].doc);

	} catch (IOException e) {
	    throw new TaxonomyException(TaxonomyErrorType.LUCENE_IO_EXCEPTION, e);
	} finally {
	    LuceneHelperTools.releaseSearcherManagerQuietly(iaviewSearcherManager, searcher);
	}
	return LuceneTaxonomyMapper.getIAViewFromLuceneDocument(hitDoc);
    }

    @Loggable
    public TopDocs performSearchWithoutAnyPostProcessing(String queryString, Query filter, Double mimimumScore,
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
	PaginatedList<InformationAssetView> paginatedListOfIAViews = new PaginatedList<InformationAssetView>(limit,
		offset, mimimumScore);
	List<InformationAssetView> docs = new ArrayList<InformationAssetView>();

	IndexSearcher isearcher = null;
	try {
	    isearcher = iaviewSearcherManager.acquire();

	    Query finalQuery = luceneHelperTools.buildSearchQueryWithFiltersIfNecessary(queryString, null);

	    TopDocs topDocs = isearcher.search(finalQuery, offset + limit);
	    logger.debug(".performSearch: found {} total hits", topDocs.totalHits);

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

    /**
     * return the total nb of docs in IAView index
     * 
     * @return
     */
    public int getTotalNbOfDocs() {
	IndexSearcher searcher = null;
	try {
	    searcher = iaviewSearcherManager.acquire();
	    IndexReader indexReader = searcher.getIndexReader();

	    return indexReader.numDocs();
	} catch (IOException ioException) {
	    throw new TaxonomyException(TaxonomyErrorType.LUCENE_IO_EXCEPTION, ioException);
	} finally {
	    LuceneHelperTools.releaseSearcherManagerQuietly(iaviewSearcherManager, searcher);
	}
    }

    /**
     * Finds the top n hits from whole Index where all results are after a
     * previous result (after)
     * 
     * @param after
     *            the last doc from previous search
     * @param nDocs
     *            nb of elements to retrieve in total
     * @return
     */
    public BrowseAllDocsResponse browseAllDocs(ScoreDoc after, int nDocs) {
	List<String> listOfDocReferences = new ArrayList<String>();
	IndexSearcher searcher = null;
	try {
	    searcher = iaviewSearcherManager.acquire();

	    TopDocs topDocs = searcher.searchAfter(after, new MatchAllDocsQuery(), nDocs, new Sort(new SortField(null,
		    Type.DOC)));
	    ScoreDoc scoreDoc = null;
	    for (int i = 0; i < topDocs.scoreDocs.length; i++) {
		scoreDoc = topDocs.scoreDocs[i];
		Document document = searcher.doc(scoreDoc.doc,
			new HashSet<String>(Arrays.asList(InformationAssetViewFields.DOCREFERENCE.toString())));
		String docReferenceFromLuceneDocument = LuceneTaxonomyMapper
			.getDocReferenceFromLuceneDocument(document);

		listOfDocReferences.add(docReferenceFromLuceneDocument);
	    }
	    return new BrowseAllDocsResponse(listOfDocReferences, scoreDoc);

	} catch (IOException ioException) {
	    throw new TaxonomyException(TaxonomyErrorType.LUCENE_IO_EXCEPTION, ioException);
	} finally {
	    LuceneHelperTools.releaseSearcherManagerQuietly(iaviewSearcherManager, searcher);
	}
    }


}

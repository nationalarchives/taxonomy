package uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.tools;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SearcherManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.exception.TaxonomyErrorType;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.exception.TaxonomyException;

@Component
public class LuceneHelperTools {

    private static final Logger logger = LoggerFactory.getLogger(LuceneHelperTools.class);

    @Value("${lucene.index.defaultTaxonomyField}")
    private String defaultTaxonomyField;

    private final Filter catalogueFilter;

    private final Analyzer iaViewSearchAnalyser;

    @Autowired
    public LuceneHelperTools(Filter catalogueFilter, Analyzer iaViewSearchAnalyser) {
	super();
	this.catalogueFilter = catalogueFilter;
	this.iaViewSearchAnalyser = iaViewSearchAnalyser;
    }

    /**
     * Release an an object from a manager without throwing any error<br/>
     * log if any error occurs
     * 
     * @param searcherManager
     * @param searcher
     */
    public static void releaseSearcherManagerQuietly(SearcherManager iaviewSearcherManager, IndexSearcher searcher) {
	try {
	    if (searcher != null) {
		iaviewSearcherManager.release(searcher);
		searcher = null;
	    }
	} catch (IOException ioe) {
	    logger.error("releaseSearcherManagerQuietly failed", ioe);
	}
    }

    /**
     * Close a writer without throwing any error<br/>
     * log if any error occurs
     * 
     * @param writer
     */
    public static void closeIndexWriterQuietly(IndexWriter writer) {
	try {
	    if (writer != null) {
		writer.close();
		writer = null;
	    }
	} catch (IOException ioe) {
	    logger.error("closeWriterQuietly failed", ioe);
	}
    }

    /**
     * Close a tokenStream without throwing any error<br/>
     * log if any error occurs
     * 
     * @param tokenStream
     */
    public static void closeTokenStreamQuietly(TokenStream tokenStream) {
	try {
	    if (tokenStream != null) {
		tokenStream.close();
	    }
	} catch (IOException e) {
	    logger.error("closeWriterQuietly failed", e);
	}
    }

    public static String removePunctuation(String string) {
	return string.replaceAll("[^a-zA-Z ]", "");
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
	QueryParser parser = new QueryParser(defaultTaxonomyField, this.iaViewSearchAnalyser);
	parser.setAllowLeadingWildcard(true);
	Query searchQuery;
	try {
	    searchQuery = parser.parse(queryString);
	} catch (ParseException e) {
	    throw new TaxonomyException(TaxonomyErrorType.INVALID_CATEGORY_QUERY, e);
	}
	return searchQuery;
    }

}

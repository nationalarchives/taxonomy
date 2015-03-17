package uk.gov.nationalarchives.discovery.taxonomy.common.service.async.task;

import java.util.concurrent.Callable;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.Category;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.exception.TaxonomyException;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.tools.LuceneHelperTools;

/**
 * Task to run a category query against the in memory index
 * 
 * @author jcharlet
 *
 */
public class RunUnitInMemoryCategoryQueryTask implements Callable<Category> {

    private Logger logger = LoggerFactory.getLogger(RunUnitInMemoryCategoryQueryTask.class);

    private final IndexSearcher searcher;
    private final Category category;
    private final LuceneHelperTools luceneHelperTools;

    public RunUnitInMemoryCategoryQueryTask(IndexSearcher searcher, Category category,
	    LuceneHelperTools luceneHelperTools) {
	super();
	this.searcher = searcher;
	this.category = category;
	this.luceneHelperTools = luceneHelperTools;
    }

    @Override
    public Category call() throws Exception {
	String queryString = category.getQry();
	try {
	    Query query = luceneHelperTools.buildSearchQuery(queryString);

	    TopDocs searchResults = searcher.search(query, 1);

	    if (searchResults.totalHits != 0) {
		logger.debug(".call: found category {}", category.getTtl());
		return category;
	    }
	} catch (TaxonomyException e) {
	    logger.debug(".call: an exception occured while parsing category query for category: {}, title: ",
		    category.getTtl(), e.getMessage());
	}
	return null;
    }

}

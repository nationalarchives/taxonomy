package uk.gov.nationalarchives.discovery.taxonomy.common.service.async.task;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.annotation.Loggable;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.Category;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.CategorisationResult;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.exception.TaxonomyException;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.IAViewRepository;

import java.util.concurrent.Callable;

import org.apache.lucene.search.Filter;
import org.apache.lucene.search.TopDocs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Task to run a category query against the index, using a filter
 * 
 * @author jcharlet
 *
 */
public class RunUnitCategoryQueryTask implements Callable<CategorisationResult> {

    private Filter filter;
    private Category category;
    private IAViewRepository iaViewRepository;
    private Logger logger = LoggerFactory.getLogger(RunUnitCategoryQueryTask.class);

    public RunUnitCategoryQueryTask(Filter filter, Category category, IAViewRepository iaViewRepository) {
	super();
	this.filter = filter;
	this.category = category;
	this.iaViewRepository = iaViewRepository;
    }

    @Loggable
    @Override
    public CategorisationResult call() throws Exception {
	logger.debug(".call: start for category: {}", category.getTtl());
	try {

	    TopDocs topDocs = iaViewRepository.performSearchWithoutAnyPostProcessing(category.getQry(), filter,
		    category.getSc(), 1, 0);
	    if (topDocs.totalHits != 0 && topDocs.scoreDocs[0].score > category.getSc()) {
		return new CategorisationResult(category.getTtl(), category.getCiaid(), topDocs.scoreDocs[0].score);
	    }
	} catch (TaxonomyException e) {
	    logger.debug(".call: an exception occured while parsing category query for category: {}, exception: {}",
		    category.getTtl(), e.getMessage());
	}
	return null;
    }

}

package uk.gov.nationalarchives.discovery.taxonomy.common.service.async;

import java.util.concurrent.Future;

import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.Category;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.CategoryWithLuceneQuery;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.CategorisationResult;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.IAViewRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.async.task.RunUnitFSCategoryQueryTask;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.async.task.RunUnitInMemoryCategoryQueryTask;

/**
 * Task manager for Taxonomy WS <br/>
 * responsible for launching asynchronous tasks and providing the needed
 * dependencies
 * 
 * See
 * {@link uk.gov.nationalarchives.discovery.taxonomy.config.AsyncConfiguration}
 * 
 * @author jcharlet
 *
 */
@Service
@ConditionalOnProperty(prefix = "lucene.categoriser.", value = "useQueryBasedCategoriser")
public class AsyncQueryBasedTaskManager {

    @Autowired
    ThreadPoolTaskExecutor fsSearchTaskExecutor;

    @Autowired
    ThreadPoolTaskExecutor memorySearchTaskExecutor;

    @Autowired
    IAViewRepository iaViewRepository;

    /**
     * launch asynchronously the @RunUnitCategoryQueryTask task
     * 
     * @param filter
     * @param category
     */
    public Future<CategorisationResult> runUnitFSCategoryQuery(Filter filter, Category category) {
	return fsSearchTaskExecutor.submit(new RunUnitFSCategoryQueryTask(filter, category, this.iaViewRepository));
    }

    /**
     * launch asynchronously the @RunUnitSearchTask task
     * 
     * @param searcher
     * @param query
     * @return
     */
    public Future<CategoryWithLuceneQuery> runUnitInMemoryCategoryQuery(IndexSearcher searcher,
	    CategoryWithLuceneQuery category) {
	return memorySearchTaskExecutor.submit(new RunUnitInMemoryCategoryQueryTask(searcher, category));
    }

}

package uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.async;

import java.util.concurrent.Future;

import org.apache.lucene.search.IndexSearcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.CategoryWithLuceneQuery;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.async.task.RunUnitInMemoryCategoryQueryTask;

/**
 * Task manager for Taxonomy query based Lucene repositories <br/>
 * responsible for launching asynchronous tasks and providing the needed
 * dependencies
 * 
 * See
 * {@link uk.gov.nationalarchives.discovery.taxonomy.config.AsyncConfiguration}
 * 
 * @author jcharlet
 *
 */
@Component
@ConditionalOnProperty(prefix = "lucene.categoriser.", value = "useQueryBasedCategoriser")
public class AsyncQueryBasedLuceneTaskManager {

    private final ThreadPoolTaskExecutor memorySearchTaskExecutor;

    @Autowired
    public AsyncQueryBasedLuceneTaskManager(ThreadPoolTaskExecutor memorySearchTaskExecutor) {
	super();
	this.memorySearchTaskExecutor = memorySearchTaskExecutor;
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

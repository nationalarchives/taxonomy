package gov.tna.discovery.taxonomy.common.service.async;

import gov.tna.discovery.taxonomy.common.domain.repository.mongo.Category;
import gov.tna.discovery.taxonomy.common.domain.service.CategorisationResult;
import gov.tna.discovery.taxonomy.common.repository.lucene.IAViewRepository;
import gov.tna.discovery.taxonomy.common.service.async.task.RunUnitCategoryQueryTask;

import java.util.concurrent.Future;

import org.apache.lucene.search.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

/**
 * Task manager for Taxonomy WS <br/>
 * responsible for launching asynchronous tasks and providing the needed
 * dependencies
 * 
 * See {@link gov.tna.discovery.taxonomy.config.AsyncConfiguration}
 * 
 * @author jcharlet
 *
 */
@Service
@ConditionalOnProperty(prefix = "lucene.categoriser.", value = "useQueryBasedCategoriser")
public class AsyncQueryBasedTaskManager {

    @Autowired
    ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    IAViewRepository iaViewRepository;

    /**
     * launch asynchronously the @RunUnitCategoryQueryTask task
     * 
     * @param filter
     * @param category
     */
    public Future<CategorisationResult> runUnitCategoryQuery(Filter filter, Category category) {
	return threadPoolTaskExecutor.submit(new RunUnitCategoryQueryTask(filter, category, this.iaViewRepository));
    }

}

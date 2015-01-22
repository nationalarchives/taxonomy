package gov.tna.discovery.taxonomy.common.service.async;

import gov.tna.discovery.taxonomy.common.repository.domain.mongo.Category;
import gov.tna.discovery.taxonomy.common.repository.lucene.IAViewRepository;
import gov.tna.discovery.taxonomy.common.repository.mongo.CategoryRepository;
import gov.tna.discovery.taxonomy.common.service.TrainingSetService;
import gov.tna.discovery.taxonomy.common.service.domain.CategorisationResult;

import java.util.Arrays;
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
@ConditionalOnProperty(prefix = "lucene.", value = "loadTSetServiceLayer")
public class AsyncTSetBasedTaskManager {

    @Autowired
    ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    TrainingSetService trainingSetService;

    @Autowired
    CategoryRepository categoryRepository;

    /**
     * Launch asynchronously the @UpdateTrainingSetDbAndIndexTask task
     * 
     * @param category
     */
    public void updateTrainingSetDbAndIndex(Category category) {
	threadPoolTaskExecutor.execute(new UpdateTrainingSetDbAndIndexTask(category, Arrays.asList(trainingSetService,
		categoryRepository)));
    }

}

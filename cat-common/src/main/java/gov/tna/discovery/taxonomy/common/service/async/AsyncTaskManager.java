package gov.tna.discovery.taxonomy.common.service.async;

import gov.tna.discovery.taxonomy.common.repository.domain.mongo.Category;
import gov.tna.discovery.taxonomy.common.repository.mongo.CategoryRepository;
import gov.tna.discovery.taxonomy.common.service.TrainingSetService;

import java.util.Arrays;
import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
public class AsyncTaskManager {

    @Autowired
    Executor threadPoolTaskExecutor;

    @Autowired
    TrainingSetService trainingSetService;

    @Autowired
    CategoryRepository categoryRepository;

    /**
     * Launch asynchronously the
     * 
     * @param category
     */
    public void updateTrainingSetDbAndIndex(Category category) {
	threadPoolTaskExecutor.execute(new UpdateTrainingSetDbAndIndexTask(category, Arrays.asList(trainingSetService,
		categoryRepository)));
    }

}

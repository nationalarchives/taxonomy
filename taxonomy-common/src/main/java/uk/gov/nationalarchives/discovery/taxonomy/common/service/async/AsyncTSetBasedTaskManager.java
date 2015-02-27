package uk.gov.nationalarchives.discovery.taxonomy.common.service.async;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.Category;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.CategoryRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.TrainingSetService;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.async.task.UpdateTrainingSetDbAndIndexTask;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

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
@ConditionalOnProperty(prefix = "lucene.categoriser.", value = "useTSetBasedCategoriser")
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

package gov.tna.discovery.taxonomy.service.async;

import gov.tna.discovery.taxonomy.repository.domain.mongo.Category;
import gov.tna.discovery.taxonomy.repository.mongo.CategoryRepository;
import gov.tna.discovery.taxonomy.repository.mongo.TrainingDocumentRepository;
import gov.tna.discovery.taxonomy.service.impl.TrainingSetService;

import java.util.Arrays;
import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

/**
 * Task executor for Taxonomy WS <br/>
 * responsible for launching asynchronous tasks
 * 
 * @author jcharlet
 *
 */
@Service
public class TaxonomyWSTaskExecutor {

    @Autowired
    private Executor asyncExecutor;

    @Autowired
    TrainingDocumentRepository trainingDocumentRepository;

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
	asyncExecutor.execute(new UpdateTrainingSetDbAndIndexTask(category, Arrays.asList(trainingDocumentRepository,
		trainingSetService, categoryRepository)));
    }

}

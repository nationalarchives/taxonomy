/**
 * 
 */
package gov.tna.discovery.taxonomy.service.async;

import java.util.List;

import gov.tna.discovery.taxonomy.repository.domain.mongo.Category;
import gov.tna.discovery.taxonomy.repository.mongo.CategoryRepository;
import gov.tna.discovery.taxonomy.repository.mongo.TrainingDocumentRepository;
import gov.tna.discovery.taxonomy.service.impl.TrainingSetService;

/**
 * Task to launch the publication of an update on a category asynchronously:<br/>
 * update training set then index it
 * 
 * @author jcharlet
 *
 */
public class UpdateTrainingSetDbAndIndexTask implements Runnable {

    private Category category;

    TrainingDocumentRepository trainingDocumentRepository;

    TrainingSetService trainingSetService;

    CategoryRepository categoryRepository;

    public UpdateTrainingSetDbAndIndexTask(Category category, List<Object> dependencies) {
	super();
	this.category = category;
	for (Object dependency : dependencies) {
	    if (dependency instanceof TrainingDocumentRepository) {
		trainingDocumentRepository = (TrainingDocumentRepository) dependency;
	    } else if (dependency instanceof TrainingSetService) {
		trainingSetService = (TrainingSetService) dependency;
	    } else if (dependency instanceof CategoryRepository) {
		categoryRepository = (CategoryRepository) dependency;
	    }
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {

	try {
	    trainingDocumentRepository.deleteByCategory(category.getTtl());
	    trainingSetService.updateTrainingSetForCategory(category, null);
	    trainingSetService.deleteAndUpdateTrainingIndexForCategory(category);
	} finally {
	    releasePublicationOnCategory(category);
	}
    }

    private void releasePublicationOnCategory(Category category) {
	category.setLck(false);
	categoryRepository.save(category);
    }

}

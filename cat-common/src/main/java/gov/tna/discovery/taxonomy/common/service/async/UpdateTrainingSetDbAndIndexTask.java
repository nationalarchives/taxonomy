/**
 * 
 */
package gov.tna.discovery.taxonomy.common.service.async;

import gov.tna.discovery.taxonomy.common.repository.domain.mongo.Category;
import gov.tna.discovery.taxonomy.common.repository.mongo.CategoryRepository;
import gov.tna.discovery.taxonomy.common.service.TrainingSetService;
import gov.tna.discovery.taxonomy.common.service.impl.TrainingSetServiceImpl;

import java.util.List;

/**
 * Task to launch the publication of an update on a category asynchronously:<br/>
 * update training set then index it
 * 
 * @author jcharlet
 *
 */
public class UpdateTrainingSetDbAndIndexTask implements Runnable {

    private Category category;

    TrainingSetService trainingSetService;

    CategoryRepository categoryRepository;

    public UpdateTrainingSetDbAndIndexTask(Category category, List<Object> dependencies) {
	super();
	this.category = category;
	for (Object dependency : dependencies) {
	    if (dependency instanceof TrainingSetServiceImpl) {
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
	    trainingSetService.deleteMongoTrainingDocumentByCategory(category.getTtl());
	    trainingSetService.updateTrainingSetForCategory(category, null, null);
	    trainingSetService.deleteAndUpdateTraingSetIndexForCategory(category);
	} finally {
	    releasePublicationOnCategory(category);
	}
    }

    private void releasePublicationOnCategory(Category category) {
	category.setLck(false);
	categoryRepository.save(category);
    }

}

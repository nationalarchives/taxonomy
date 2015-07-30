/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/**
 * 
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.service.async.task;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.Category;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.CategoryRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.TrainingSetService;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.impl.TrainingSetServiceImpl;

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

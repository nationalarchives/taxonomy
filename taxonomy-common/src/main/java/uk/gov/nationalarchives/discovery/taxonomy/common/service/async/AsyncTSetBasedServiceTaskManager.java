/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.service.async;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.Category;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.CategoryRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.TrainingSetService;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.async.task.UpdateTrainingSetDbAndIndexTask;

/**
 * Task manager for Taxonomy tset based services <br/>
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
public class AsyncTSetBasedServiceTaskManager {

    private final ThreadPoolTaskExecutor fsSearchTaskExecutor;

    private final CategoryRepository categoryRepository;

    @Autowired
    public AsyncTSetBasedServiceTaskManager(ThreadPoolTaskExecutor fsSearchTaskExecutor,
	    CategoryRepository categoryRepository) {
	super();
	this.fsSearchTaskExecutor = fsSearchTaskExecutor;
	this.categoryRepository = categoryRepository;
    }

    /**
     * Launch asynchronously the @UpdateTrainingSetDbAndIndexTask task
     * 
     * @param category
     */
    public void updateTrainingSetDbAndIndex(Category category, TrainingSetService trainingSetService) {
	fsSearchTaskExecutor.execute(new UpdateTrainingSetDbAndIndexTask(category, Arrays.asList(trainingSetService,
		categoryRepository)));
    }

}

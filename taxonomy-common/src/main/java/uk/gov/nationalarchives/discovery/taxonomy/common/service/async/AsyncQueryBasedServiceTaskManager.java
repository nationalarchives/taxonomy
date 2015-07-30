/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.service.async;

import java.util.concurrent.Future;

import org.apache.lucene.search.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.Category;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.CategorisationResult;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.IAViewRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.async.task.RunUnitFSCategoryQueryTask;

/**
 * Task manager for Taxonomy query based services <br/>
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
public class AsyncQueryBasedServiceTaskManager {

    private final ThreadPoolTaskExecutor fsSearchTaskExecutor;
    private final IAViewRepository iaViewRepository;

    @Autowired
    public AsyncQueryBasedServiceTaskManager(ThreadPoolTaskExecutor fsSearchTaskExecutor,
	    IAViewRepository iaViewRepository) {
	super();
	this.fsSearchTaskExecutor = fsSearchTaskExecutor;
	this.iaViewRepository = iaViewRepository;
    }

    /**
     * launch asynchronously the @RunUnitCategoryQueryTask task
     * 
     * @param filter
     * @param category
     */
    public Future<CategorisationResult> runUnitFSCategoryQuery(Filter filter, Category category) {
	return fsSearchTaskExecutor.submit(new RunUnitFSCategoryQueryTask(filter, category, this.iaViewRepository));
    }

}

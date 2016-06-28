/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.service.async.task;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.annotation.Loggable;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.exception.TaxonomyException;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.Category;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.CategorisationResult;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.IAViewRepository;

import java.util.concurrent.Callable;

/**
 * Task to run a category query against the index, using a filter
 * 
 * @author jcharlet
 *
 */
public class RunUnitFSCategoryQueryTask implements Callable<CategorisationResult> {

    private Query filter;
    private Category category;
    private IAViewRepository iaViewRepository;
    private Logger logger = LoggerFactory.getLogger(RunUnitFSCategoryQueryTask.class);

    public RunUnitFSCategoryQueryTask(Query filter, Category category, IAViewRepository iaViewRepository) {
	super();
	this.filter = filter;
	this.category = category;
	this.iaViewRepository = iaViewRepository;
    }

    @Loggable
    @Override
    public CategorisationResult call() throws Exception {
	logger.debug(".call: start for category: {}", category.getTtl());
	try {

	    TopDocs topDocs = iaViewRepository.performSearchWithoutAnyPostProcessing(category.getQry(), filter,
		    category.getSc(), 1, 0);
	    if (topDocs.totalHits != 0 && topDocs.scoreDocs[0].score > category.getSc()) {
		return new CategorisationResult(category.getTtl(), category.getCiaid(), topDocs.scoreDocs[0].score);
	    }
	} catch (TaxonomyException e) {
	    logger.debug(".call: an exception occured while parsing category query for category: {}, exception: {}",
		    category.getTtl(), e.getMessage());
	}
	return null;
    }

}

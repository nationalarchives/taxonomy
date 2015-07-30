/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.async.task;

import java.util.concurrent.Callable;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.exception.TaxonomyException;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.CategoryWithLuceneQuery;

/**
 * Task to run a category query against the in memory index
 * 
 * @author jcharlet
 *
 */
public class RunUnitInMemoryCategoryQueryTask implements Callable<CategoryWithLuceneQuery> {

    private Logger logger = LoggerFactory.getLogger(RunUnitInMemoryCategoryQueryTask.class);

    private final IndexSearcher searcher;
    private final CategoryWithLuceneQuery category;

    public RunUnitInMemoryCategoryQueryTask(IndexSearcher searcher, CategoryWithLuceneQuery category) {
	super();
	this.searcher = searcher;
	this.category = category;
    }

    @Override
    public CategoryWithLuceneQuery call() throws Exception {
	try {
	    TopDocs searchResults = searcher.search(category.getParsedQry(), 1);

	    if (searchResults.totalHits != 0) {
		logger.debug(".call: found category {}", category.getTtl());
		return category;
	    }
	} catch (TaxonomyException e) {
	    logger.debug(".call: an exception occured while parsing category query for category: {}, title: ",
		    category.getTtl(), e.getMessage());
	}
	return null;
    }

}

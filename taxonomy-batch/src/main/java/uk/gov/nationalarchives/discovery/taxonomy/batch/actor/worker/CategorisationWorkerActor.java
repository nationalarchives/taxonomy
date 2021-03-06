/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.batch.actor.worker;

import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.nationalarchives.discovery.taxonomy.BatchApplication;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.exception.TaxonomyException;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.Category;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.CategoryWithLuceneQuery;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.actor.*;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.lucene.tools.LuceneHelperTools;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.CategoryRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.service.CategoriserService;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class CategorisationWorkerActor extends UntypedActor {

    private final CategoriserService categoriserService;

    private final LuceneHelperTools luceneHelperTools;

    private final CategoryRepository categoryRepository;

    private Logger logger;

    private List<CategoryWithLuceneQuery> cachedCategories = new ArrayList<CategoryWithLuceneQuery>();

    public CategorisationWorkerActor(String supervisorAddress, CategoriserService categoriserService,
	    LuceneHelperTools luceneHelperTools, CategoryRepository categoryRepository) {
	super();
	this.categoriserService = categoriserService;
	this.luceneHelperTools = luceneHelperTools;
	this.categoryRepository = categoryRepository;

	this.logger = LoggerFactory.getLogger(CategorisationWorkerActor.class);

	cacheCategoryQueries();

	ActorSelection actorSelection = getContext().actorSelection(supervisorAddress);
	actorSelection.tell(new RegisterWorker(), getSelf());
	actorSelection.tell(new GimmeWork(), getSelf());
    }

    private void cacheCategoryQueries() {
	for (Category category : categoryRepository.findAll()) {
	    CategoryWithLuceneQuery cachedCategory = new CategoryWithLuceneQuery(category,
		    luceneHelperTools.buildSearchQuery(category.getQry()));
	    cachedCategories.add(cachedCategory);
	}
    }

    @Override
    public void onReceive(Object message) throws Exception {
	if (message instanceof CurrentlyBusy) {
	    this.logger.warn("Supervisor is currently busy.");
	} else if (message instanceof WorkAvailable) {
	    this.logger.debug("received WorkAvailable");
	    getSender().tell(new GimmeWork(), getSelf());

	} else if (message instanceof CategoriseDocuments) {
	    this.logger.debug("received CategoriseDocuments");
	    String[] docReferences = ((CategoriseDocuments) message).getDocReferences();
	    this.logger.info(".onReceive> {}  categorising those: {}", getSelf().hashCode(),
		    ArrayUtils.toString(docReferences));
	    for (String docReference : docReferences) {
		try {
		    this.categoriserService.categoriseSingle(docReference, cachedCategories);
		} catch (TaxonomyException e) {
		    logger.error("an error occured while processing Document: {}", docReference, e);
		}
	    }
	    this.logger.info(".onReceive<  treatment completed");

	    getSender().tell(new GimmeWork(), getSelf());
    } else if (message instanceof Shutdown) {
        this.logger.info("shutting down, as requested by supervisor");
        BatchApplication.exit();
	} else {
	    unhandled(message);
	}
    }

}

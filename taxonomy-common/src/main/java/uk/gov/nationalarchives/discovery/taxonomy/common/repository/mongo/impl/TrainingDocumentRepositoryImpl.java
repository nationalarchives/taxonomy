/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.impl;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.TrainingDocument;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.TrainingDocumentRepositoryCustom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.mongodb.WriteResult;

@Repository
@ConditionalOnProperty(prefix = "lucene.categoriser.", value = "useTSetBasedCategoriser")
public class TrainingDocumentRepositoryImpl implements TrainingDocumentRepositoryCustom {

    private final MongoTemplate categoriesMongoTemplate;

    @Autowired
    public TrainingDocumentRepositoryImpl(MongoTemplate categoriesMongoTemplate) {
	super();
	this.categoriesMongoTemplate = categoriesMongoTemplate;
    }

    @Override
    public int deleteByCategory(String categoryName) {
	Query query = new Query(Criteria.where("CATEGORY").is(categoryName));
	WriteResult writeResult = categoriesMongoTemplate.remove(query, TrainingDocument.class);
	return writeResult.getN();
    }

}

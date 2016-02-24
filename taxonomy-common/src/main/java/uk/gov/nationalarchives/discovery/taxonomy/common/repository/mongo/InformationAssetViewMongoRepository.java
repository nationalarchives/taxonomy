/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo;

import org.springframework.data.repository.CrudRepository;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.MongoInformationAssetView;

/**
 * Mongo repository dedicated to IAViews: contains the last results of
 * categorisation for every document categorised<br/>
 * Does not follow usual naming convention in order not to be confused with
 * IAViewRepository dedicated to Solr
 * 
 * @author jcharlet
 *
 */
public interface InformationAssetViewMongoRepository extends CrudRepository<MongoInformationAssetView, String>,
        InformationAssetViewMongoRepositoryCustom {

    /**
     * find by doc reference
     * 
     * @param docReference
     * @return
     */
    public MongoInformationAssetView findByDocReference(String docReference);

}

/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.service;

import java.util.List;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.IAViewUpdate;

/**
 * Service dedicated to make updates on Solr Cloud (cluster of servers used by
 * Discovery) to apply results from categorisation
 * 
 * @author jcharlet
 *
 */
public interface UpdateSolrCloudService {

    /**
     * update a document with the results of last performed categorisation
     * 
     * @param docReference
     *            of the document to update
     */
    void updateCategoriesOnIAView(String docReference);

    /**
     * bulk update documents with the results of last performed categorisation
     * 
     * @param listOfIAViewUpdatesToProcess
     *            of the documents to update
     */
    void bulkUpdateCategoriesOnIAViews(List<IAViewUpdate> listOfIAViewUpdatesToProcess);

}

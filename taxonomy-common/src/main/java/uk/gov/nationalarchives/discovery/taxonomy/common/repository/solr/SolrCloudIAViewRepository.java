/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.repository.solr;

import java.util.List;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;

/**
 * Repository dedicated to Solr Cloud (cluster of servers used by Discovery),
 * InformationAssetView collection
 * 
 * @author jcharlet
 *
 */
public interface SolrCloudIAViewRepository {

    /**
     * Get an IAView by DocReference
     * 
     * @param docReference
     *            its unique docReference
     * @return the solr document
     */
    SolrDocument getByDocReference(String docReference);

    /**
     * save a document in solr cloud
     * 
     * @param document
     *            to save
     */
    void save(SolrInputDocument document);

    /**
     * save a list of documents in Solr cloud
     * 
     * @param documents
     *            to save
     */
    void saveAll(List<SolrInputDocument> documents);

}

/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.repository.solr.impl;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.exception.TaxonomyErrorType;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.exception.TaxonomyException;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.solr.SolrTaxonomyIAViewRepository;

import java.io.IOException;

@Repository
@ConditionalOnProperty(prefix = "solr.taxonomy", value = "host")
public class SolrTaxonomyIAViewRepositoryImpl implements SolrTaxonomyIAViewRepository {

    private final SolrClient solrTaxonomyServer;

    @Autowired
    public SolrTaxonomyIAViewRepositoryImpl(SolrClient solrTaxonomyServer) {
	super();
	this.solrTaxonomyServer = solrTaxonomyServer;
    }

    @Override
    public void commit() {
	try {
	    solrTaxonomyServer.commit();
	} catch (SolrServerException | IOException e) {
	    throw new TaxonomyException(TaxonomyErrorType.SOLR_WRITE_EXCEPTION, e);
	}
    }

}

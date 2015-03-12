package uk.gov.nationalarchives.discovery.taxonomy.common.repository.solr.impl;

import java.io.IOException;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.exception.TaxonomyErrorType;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.exception.TaxonomyException;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.solr.SolrTaxonomyIAViewRepository;

@Repository
@ConditionalOnProperty(prefix = "solr.taxonomy", value = "host")
public class SolrTaxonomyIAViewRepositoryImpl implements SolrTaxonomyIAViewRepository {

    @Autowired
    private SolrServer solrTaxonomyServer;

    @Override
    public void commit() {
	try {
	    solrTaxonomyServer.commit();
	} catch (SolrServerException | IOException e) {
	    throw new TaxonomyException(TaxonomyErrorType.SOLR_WRITE_EXCEPTION, e);
	}
    }

}
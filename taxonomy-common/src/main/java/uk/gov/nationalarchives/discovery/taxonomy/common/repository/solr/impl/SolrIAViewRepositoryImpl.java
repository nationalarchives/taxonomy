package uk.gov.nationalarchives.discovery.taxonomy.common.repository.solr.impl;

import java.io.IOException;
import java.util.List;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.lucene.InformationAssetViewFields;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.exception.TaxonomyErrorType;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.exception.TaxonomyException;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.solr.SolrIAViewRepository;

@Repository
public class SolrIAViewRepositoryImpl implements SolrIAViewRepository {

    @Autowired
    private SolrServer solrServer;

    // private static final Logger logger =
    // LoggerFactory.getLogger(SolrIAViewRepositoryImpl.class);

    @Override
    public SolrDocument getByDocReference(String docReference) {
	SolrQuery query = createSolrQueryForDocReference(docReference);

	SolrDocumentList results = submitQueryToSolr(query);

	validateSolrResponseForGetByDocReferenceRequest(docReference, results);

	if (results.size() == 0) {
	    return null;
	}
	return results.get(0);
    }

    private void validateSolrResponseForGetByDocReferenceRequest(String docReference, SolrDocumentList results) {
	if (results == null) {
	    throw new TaxonomyException(TaxonomyErrorType.SOLR_READ_EXCEPTION,
		    "Invalid response, results object is null");
	} else if (results.size() > 1) {
	    throw new TaxonomyException(TaxonomyErrorType.SOLR_READ_EXCEPTION,
		    "There should be only 1 IAView for docReference: " + docReference + " but there were "
			    + results.size() + " results");
	}
    }

    private SolrDocumentList submitQueryToSolr(SolrParams params) {
	QueryResponse response;
	try {
	    response = solrServer.query(params);
	} catch (SolrServerException e) {
	    throw new TaxonomyException(TaxonomyErrorType.SOLR_READ_EXCEPTION, e);
	}
	SolrDocumentList results = response.getResults();
	return results;
    }

    private SolrQuery createSolrQueryForDocReference(String docReference) {
	SolrQuery query = new SolrQuery();
	String queryString = new StringBuilder().append(InformationAssetViewFields.DOCREFERENCE.toString()).append(":")
		.append(docReference).toString();
	query.setQuery(queryString);
	return query;
    }

    @Override
    public void save(SolrInputDocument document) {
	try {
	    solrServer.add(document);

	    solrServer.commit();
	} catch (SolrServerException | IOException e) {
	    throw new TaxonomyException(TaxonomyErrorType.SOLR_WRITE_EXCEPTION, e);
	}
    }

    @Override
    public void saveAll(List<SolrInputDocument> documents) {
	try {
	    solrServer.add(documents);

	    solrServer.commit();
	} catch (SolrServerException | IOException e) {
	    throw new TaxonomyException(TaxonomyErrorType.SOLR_WRITE_EXCEPTION, e);
	}
    }

    // @Override
    // public int deleteByCategory(String catelgoryName) {
    // Query query = new Query(Criteria.where("CATEGORY").is(categoryName));
    // WriteResult writeResult = mongoTemplate.remove(query,
    // TrainingDocument.class);
    // return writeResult.getN();
    // }

}

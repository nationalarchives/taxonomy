package uk.gov.nationalarchives.discovery.taxonomy.common.repository.solr;

import java.util.List;

import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;

/**
 * Repository dedicated to Solr InformationAssetView collection
 * 
 * @author jcharlet
 *
 */
public interface SolrIAViewRepository {

    /**
     * Get an IAView by DocReference
     * 
     * @param docReference
     *            its unique docReference
     * @return the solr document
     */
    SolrDocument getByDocReference(String docReference);

    /**
     * save a document in solr and commit it
     * 
     * @param document
     *            to save
     */
    void save(SolrInputDocument document);

    /**
     * save a list of documents in Solr and commit it
     * 
     * @param documents
     *            to save
     */
    void saveAll(List<SolrInputDocument> documents);

}

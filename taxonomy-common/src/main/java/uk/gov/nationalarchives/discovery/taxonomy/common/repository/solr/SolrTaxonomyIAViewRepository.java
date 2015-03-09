package uk.gov.nationalarchives.discovery.taxonomy.common.repository.solr;

/**
 * Repository dedicated to taxonomy dedicated Solr server, InformationAssetView
 * collection
 * 
 * @author jcharlet
 *
 */
public interface SolrTaxonomyIAViewRepository {

    /**
     * Send a commit request to apply all pending changes
     */
    void commit();
}

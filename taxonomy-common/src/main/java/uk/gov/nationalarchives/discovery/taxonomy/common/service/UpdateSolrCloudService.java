package uk.gov.nationalarchives.discovery.taxonomy.common.service;

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
     * @param docReferences
     *            of the documents to update
     */
    void bulkUpdateCategoriesOnIAViews(String[] docReferences);

}

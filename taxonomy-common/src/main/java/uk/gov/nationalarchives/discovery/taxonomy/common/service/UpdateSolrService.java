package uk.gov.nationalarchives.discovery.taxonomy.common.service;

/**
 * Service dedicated to make updates on Solr Server to apply results from
 * categorisation
 * 
 * @author jcharlet
 *
 */
public interface UpdateSolrService {

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

    /**
     * bulk update documents with the results of categorisation performed during
     * the given time range
     * 
     * @param start
     *            the timestamp of the first update to take into account
     *            (inclusive)
     * @param end
     *            the timestamp of the last update to take into account
     *            (inclusive)
     */
    void bulkUpdateCategoriesOnIAViewsOnTimeRange(long start, long end);
}

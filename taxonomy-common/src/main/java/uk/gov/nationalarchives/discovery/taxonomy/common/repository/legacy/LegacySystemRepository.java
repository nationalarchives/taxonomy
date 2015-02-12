package uk.gov.nationalarchives.discovery.taxonomy.common.repository.legacy;

import java.util.Map;

/**
 * Repository dedicated to the retrieval of documents on the legacy system and
 * their categories
 * 
 * @author jcharlet
 *
 */
public interface LegacySystemRepository {

    /**
     * Retrieve the "subjects" (categories) from the legacy website for a
     * document<br/>
     * The request actually looks for a document with the given catdocref and
     * analyses only the first result, so the document could not be found or
     * another one could be retreived but we tolerate it
     * 
     * @param catdocref
     * @return
     */
    @Deprecated
    public String[] getLegacyCategoriesForCatDocRef(String catdocref);

    /**
     * find legacy documents by query
     * 
     * @param qry
     * @param page
     * @return a map of iaids with their related subjets
     */
    public Map<String, String[]> findLegacyDocumentsByCategory(String qry, Integer page);

}
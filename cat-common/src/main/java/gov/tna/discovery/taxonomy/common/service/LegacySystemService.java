package gov.tna.discovery.taxonomy.common.service;

import java.util.Map;

public interface LegacySystemService {

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
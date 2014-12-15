package gov.tna.discovery.taxonomy.common.service;

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
    public abstract String[] getLegacyCategoriesForCatDocRef(String catdocref);

}
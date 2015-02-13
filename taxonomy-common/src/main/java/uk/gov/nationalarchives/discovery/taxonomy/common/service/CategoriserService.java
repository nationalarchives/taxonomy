package uk.gov.nationalarchives.discovery.taxonomy.common.service;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.CategorisationResult;

import java.io.IOException;
import java.util.List;

public interface CategoriserService<T extends CategorisationResult> {

    /**
     * Categorise the whole IA collection
     * 
     * @throws IOException
     */
    @Deprecated
    public void testCategoriseIAViewSolrIndex() throws IOException;

    /**
     * Preview the categorisation of a document
     * 
     * @param docReference
     * @return {@link CategorisationResult}
     */
    public List<T> testCategoriseSingle(String docReference);

    /**
     * Categorise a document and save the found categories
     * 
     * @param docReference
     * @return
     */
    public List<T> categoriseSingle(String docReference);

}
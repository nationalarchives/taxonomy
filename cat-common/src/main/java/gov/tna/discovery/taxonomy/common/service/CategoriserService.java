package gov.tna.discovery.taxonomy.common.service;

import gov.tna.discovery.taxonomy.common.domain.repository.lucene.InformationAssetView;
import gov.tna.discovery.taxonomy.common.domain.service.CategorisationResult;

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
     * @param iaView
     * @return {@link CategorisationResult}
     */
    public List<T> testCategoriseSingle(InformationAssetView iaView);

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
     * @param iaView
     * @return
     */
    public List<T> categoriseSingle(InformationAssetView iaView);

    /**
     * Categorise a document and save the found categories
     * 
     * @param docReference
     * @return
     */
    public List<T> categoriseSingle(String docReference);

}
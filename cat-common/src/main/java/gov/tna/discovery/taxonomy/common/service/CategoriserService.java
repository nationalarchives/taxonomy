package gov.tna.discovery.taxonomy.common.service;

import gov.tna.discovery.taxonomy.common.repository.domain.lucene.InformationAssetView;
import gov.tna.discovery.taxonomy.common.service.domain.CategorisationResult;
import gov.tna.discovery.taxonomy.common.service.domain.TSetBasedCategorisationResult;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;

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
     * @param catDocRef
     * @return {@link CategorisationResult}
     */
    public List<T> testCategoriseSingle(String catDocRef);

}
package gov.tna.discovery.taxonomy.common.service;

import gov.tna.discovery.taxonomy.common.repository.domain.lucene.InformationAssetView;
import gov.tna.discovery.taxonomy.common.service.domain.CategorisationResult;

import java.io.IOException;
import java.util.List;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;

public interface CategoriserService {

    /**
     * categorise a document by running the MLT process against the training set<br/>
     * deprecated, use testCategoriseSingle instead
     * 
     * @param catdocref
     *            IAID
     * @throws IOException
     * @throws ParseException
     */
    @Deprecated
    public List<CategorisationResult> categoriseIAViewSolrDocument(String catdocref);

    /**
     * Categorise the whole IA collection
     * 
     * @throws IOException
     */
    public void testCategoriseIAViewSolrIndex() throws IOException;

    /**
     * run More Like This process on a document by comparing its description to
     * the description of all items of the training set<br/>
     * currently we get a fixed number of the top results
     * 
     * @param document
     *            document being tested
     * @return
     * @throws IOException
     */
    public List<CategorisationResult> runMlt(Document document);

    /**
     * Preview the categorisation of a document
     * 
     * @param iaView
     * @return {@link CategorisationResult}
     */
    public List<CategorisationResult> testCategoriseSingle(InformationAssetView iaView);

}
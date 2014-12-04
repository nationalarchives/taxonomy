package gov.tna.discovery.taxonomy.service;

import gov.tna.discovery.taxonomy.repository.domain.lucene.InformationAssetView;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;

public interface Categoriser {

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
    public Map<String, Float> categoriseIAViewSolrDocument(String catdocref);

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
    Map<String, Float> runMlt(Document document);

    /**
     * Preview the categorisation of a document
     * 
     * @param iaView
     * @return
     */
    public Map<String, Float> testCategoriseSingle(InformationAssetView iaView);

}
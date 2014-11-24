package gov.tna.discovery.taxonomy.service;

import gov.tna.discovery.taxonomy.repository.domain.lucene.InformationAssetView;

import java.io.IOException;
import java.io.Reader;
import java.util.Map;

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
    public abstract Map<String, Float> categoriseIAViewSolrDocument(String catdocref);

    /**
     * Categorise the whole IA collection
     * 
     * @throws IOException
     */
    public abstract void testCategoriseIAViewSolrIndex() throws IOException;

    /**
     * run More Like This process on a document by comparing its description to
     * the description of all items of the training set<br/>
     * currently we get a fixed number of the top results
     * 
     * @param reader
     *            reader of the document being tested
     * @return
     * @throws IOException
     */
    // TODO 1 check and update fields that are being retrieved to create
    // training set, used for MLT (run MLT on title, context desc and desc at
    // least. returns results by score not from a fixed number)
    public abstract Map<String, Float> runMlt(Reader reader);

    /**
     * Preview the categorisation of a document
     * 
     * @param iaView
     * @return
     */
    public abstract Map<String, Float> testCategoriseSingle(InformationAssetView iaView);

}
package uk.gov.nationalarchives.discovery.taxonomy.common.service;

import java.util.Date;
import java.util.List;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.CategoryWithLuceneQuery;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.IAViewUpdate;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.CategorisationResult;

/**
 * Service dedicated to the categorisation of documents
 * 
 * @author jcharlet
 *
 * @param <T>
 */
public interface CategoriserService<T extends CategorisationResult> {

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

    /**
     * Categorise a document and save the found categories
     * 
     * @param docReference
     * @param cachedCategories
     *            on batch processes, to avoid retrieving and parsing all
     *            category queries, provide cached categories
     */
    public List<T> categoriseSingle(String docReference, List<CategoryWithLuceneQuery> cachedCategories);

    /**
     * get new categorised documents from (including) date <br/>
     * if parameters are null, it will look for any document
     * 
     * @param date
     * @param limit
     * @return
     */
    List<IAViewUpdate> getNewCategorisedDocumentsFromDate(Date fromDate, int limit);

    /**
     * find last update on categories on iaviews from mongo db
     * 
     * @return
     */
    public IAViewUpdate findLastIAViewUpdate();

    /**
     * refresh the index used for categorisation.<br/>
     * implies to commit changes on Solr dedicated server AND update the index
     * reader on Lucene<br/>
     * It is necessary to call that method if the document to categorise was
     * indexed right before that call
     */
    public void refreshTaxonomyIndex();

    /**
     * get new categorised documents since document
     * 
     * @param afterIAViewUpdate
     * @param limit
     * @return
     */
    List<IAViewUpdate> getNewCategorisedDocumentsAfterDocument(IAViewUpdate afterIAViewUpdate, int limit);

}
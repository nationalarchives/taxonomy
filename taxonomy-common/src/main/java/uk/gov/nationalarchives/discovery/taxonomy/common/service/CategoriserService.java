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
     * get new categorised documents from (including) date to nb of seconds in
     * past<br/>
     * if date is null, it will look for any document
     * 
     * @param date
     * @param nbOfSecondsInPast
     *            using this parameter we do not risk missing documents that
     *            were added from different servers
     * @param limit
     * @return
     */
    List<IAViewUpdate> getNewCategorisedDocumentsFromDateToNSecondsInPast(Date date, int nbOfSecondsInPast, int limit);

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
     * get new categorised documents since document and up to nb of seconds in
     * past
     * 
     * @param afterIAViewUpdate
     * @param nbOfSecondsInPast
     *            using this parameter we do not risk missing documents that
     *            were added from different servers
     * @param limit
     * @return
     */
    List<IAViewUpdate> getNewCategorisedDocumentsAfterDocumentAndUpToNSecondsInPast(IAViewUpdate afterIAViewUpdate,
	    int nbOfSecondsInPast, int limit);

}
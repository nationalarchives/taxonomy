package uk.gov.nationalarchives.discovery.taxonomy.common.service;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.IAViewUpdate;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.CategorisationResult;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;

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
     * Check if any document was categorised since one date
     * 
     * @param date
     * @return
     */
    public boolean hasNewCategorisedDocumentsSinceDate(Date date);

    /**
     * get page of IAViewUpdates since date
     * 
     * @param pageNumber
     * @param lastIAViewUpdateProcessedTime
     * @return
     */
    public Page<IAViewUpdate> getPageOfNewCategorisedDocumentsSinceDate(int pageNumber,
	    Date lastIAViewUpdateProcessedTime);

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

}
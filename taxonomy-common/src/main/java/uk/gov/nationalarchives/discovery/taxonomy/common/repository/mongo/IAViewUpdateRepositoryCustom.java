package uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo;

import java.util.Date;
import java.util.List;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.IAViewUpdate;

/**
 * Custom repository for IAViewUpdates, containing methods to implememt manually
 * 
 * @author jcharlet
 *
 */
public interface IAViewUpdateRepositoryCustom {

    /**
     * find last IAViewUpdate document
     * 
     * @return
     */
    IAViewUpdate findLastIAViewUpdate();

    /**
     * find elements which created after given document, AND after given date<br/>
     * if one parameter is empty, it will not be taken into account in the query
     * 
     * @param afterIAViewUpdate
     * @param ltDate
     * @param limit
     * @return
     */
    List<IAViewUpdate> findDocumentsCreatedAfterDocumentAndCreatedBeforeDate(IAViewUpdate afterIAViewUpdate,
	    Date ltDate, Integer limit);

    /**
     * find a document by docReference and remove it
     * 
     * @param docReference
     */
    void findAndRemoveByDocReference(String docReference);

    /**
     * find elements where date is after than first parameter, AND date before
     * than second parameter<br/>
     * if one parameter is empty, it will not be taken into account in the query
     * 
     * @param gteDate
     * @param ltDate
     * @param limit
     * @return
     */
    List<IAViewUpdate> findDocumentsCreatedAfterDateAndCreatedBeforeDate(Date gtDate, Date ltDate, Integer limit);

}

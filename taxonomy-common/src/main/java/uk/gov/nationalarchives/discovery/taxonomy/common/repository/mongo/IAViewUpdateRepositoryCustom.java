/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
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
     * find elements from (including) date, AND before second date provided<br/>
     * if one parameter is empty, it will not be taken into account in the query
     * 
     * @param gteDate
     * @param ltDate
     * @param limit
     * @return
     */
    List<IAViewUpdate> findDocumentsCreatedFromDateAndCreatedBeforeDate(Date gteDate, Date ltDate, Integer limit);

}

package uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo;

import java.util.List;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.IAViewUpdate;

public interface IAViewUpdateRepositoryCustom {

    /**
     * find last IAViewUpdate document
     * 
     * @return
     */
    IAViewUpdate findLastIAViewUpdate();

    /**
     * find elements with element more recent than the one provided, limit the
     * number of results
     * 
     * @param id
     * @param limit
     * @return
     */
    List<IAViewUpdate> findByDocumentMoreRecentThan(IAViewUpdate lastIAViewUpdate, Integer limit);
}

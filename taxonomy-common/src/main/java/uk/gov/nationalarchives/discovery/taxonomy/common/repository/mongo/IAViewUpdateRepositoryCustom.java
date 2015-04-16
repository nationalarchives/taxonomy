package uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo;

import java.util.List;

import org.bson.types.ObjectId;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.IAViewUpdate;

public interface IAViewUpdateRepositoryCustom {

    /**
     * find last IAViewUpdate document
     * 
     * @return
     */
    IAViewUpdate findLastIAViewUpdate();

    /**
     * find elements with id more recent than the one provided, limit the number
     * of results
     * 
     * @param id
     * @param limit
     * @return
     */
    List<IAViewUpdate> findByIdGreaterThan(ObjectId id, Integer limit);
}

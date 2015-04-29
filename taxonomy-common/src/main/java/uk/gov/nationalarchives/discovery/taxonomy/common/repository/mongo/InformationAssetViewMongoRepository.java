package uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo;

import org.springframework.data.repository.CrudRepository;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.MongoInformationAssetView;

/**
 * Mongo repository dedicated to IAViews: contains the last results of
 * categorisation for every document categorised<br/>
 * Does not follow usual naming convention in order not to be confused with
 * IAViewRepository dedicated to Solr
 * 
 * @author jcharlet
 *
 */
public interface InformationAssetViewMongoRepository extends CrudRepository<MongoInformationAssetView, String> {

    /**
     * find by doc reference
     * 
     * @param docReference
     * @return
     */
    public MongoInformationAssetView findByDocReference(String docReference);

}

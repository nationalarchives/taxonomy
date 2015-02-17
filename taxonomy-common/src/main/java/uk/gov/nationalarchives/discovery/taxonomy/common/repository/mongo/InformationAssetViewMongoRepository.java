package uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.MongoInformationAssetView;

@Repository
public interface InformationAssetViewMongoRepository extends CrudRepository<MongoInformationAssetView, String> {

    public MongoInformationAssetView findByDocReference(String docReference);
}

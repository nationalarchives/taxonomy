package uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo;

import org.springframework.data.repository.CrudRepository;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.MongoInformationAssetView;

public interface InformationAssetViewMongoRepository extends CrudRepository<MongoInformationAssetView, String> {

    public MongoInformationAssetView findByDocReference(String docReference);
}

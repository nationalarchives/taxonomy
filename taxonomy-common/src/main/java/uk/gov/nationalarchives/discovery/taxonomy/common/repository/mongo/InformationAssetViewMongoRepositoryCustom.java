package uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.MongoInformationAssetView;

import java.util.List;

/**
 * Created by jcharlet on 2/22/16.
 */
public interface InformationAssetViewMongoRepositoryCustom {
    List<MongoInformationAssetView> findUntaggedDocumentsBySeries(String seriesIaid, Integer limit, Integer offset);
}

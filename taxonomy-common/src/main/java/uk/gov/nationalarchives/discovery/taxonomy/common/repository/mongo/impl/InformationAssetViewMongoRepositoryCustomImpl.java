package uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.MongoInformationAssetView;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.InformationAssetViewMongoRepositoryCustom;

import java.util.List;

/**
 * Created by jcharlet on 2/22/16.
 */
@Repository
public class InformationAssetViewMongoRepositoryCustomImpl implements InformationAssetViewMongoRepositoryCustom {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public InformationAssetViewMongoRepositoryCustomImpl(MongoTemplate mongoTemplate) {
        super();
        this.mongoTemplate = mongoTemplate;
    }

    public List<MongoInformationAssetView> findUntaggedDocumentsBySeries(String seriesIaid, Integer limit, Integer
            offset) {
        Query query = Query.query(Criteria.where("series").is(seriesIaid).and("categories").size(0));
        query.limit(limit);
        query.skip(offset);
        query.with(new Sort(Sort.Direction.ASC, "_id"));
        return mongoTemplate.find(query, MongoInformationAssetView.class);
    }

}

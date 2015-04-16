package uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.impl;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.IAViewUpdate;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.IAViewUpdateRepositoryCustom;

@Repository
public class IAViewUpdateRepositoryImpl implements IAViewUpdateRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public IAViewUpdateRepositoryImpl(MongoTemplate mongoTemplate) {
	super();
	this.mongoTemplate = mongoTemplate;
    }

    @Override
    public IAViewUpdate findLastIAViewUpdate() {
	Query query = new Query();
	query.limit(1);
	query.with(new Sort(Sort.Direction.DESC, IAViewUpdate.ID_FIELDNAME));
	return mongoTemplate.findOne(query, IAViewUpdate.class);
    }

    @Override
    public List<IAViewUpdate> findByIdGreaterThan(ObjectId id, Integer limit) {
	Query query = new Query(Criteria.where(IAViewUpdate.ID_FIELDNAME).gt(id));
	query.limit(limit);
	query.with(new Sort(Sort.Direction.ASC, IAViewUpdate.ID_FIELDNAME));
	return mongoTemplate.find(query, IAViewUpdate.class);
    }

}

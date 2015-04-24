package uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
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
	query.with(new Sort(Sort.Direction.DESC, IAViewUpdate.FIELD_ID));
	return mongoTemplate.findOne(query, IAViewUpdate.class);
    }

    @Override
    public List<IAViewUpdate> findWhereDateGreaterThanEqualAndLowerThan(Date gteDate, Date ltDate, Integer limit) {
	List<Criteria> listOfCriterias = new ArrayList<Criteria>();
	if (gteDate != null) {
	    listOfCriterias.add(Criteria.where(IAViewUpdate.FIELD_CREATIONDATE).gte(gteDate));
	}
	if (ltDate != null) {
	    listOfCriterias.add(Criteria.where(IAViewUpdate.FIELD_CREATIONDATE).lt(ltDate));
	}
	Query query = new Query(new Criteria().andOperator(listOfCriterias.toArray(new Criteria[0])));

	query.limit(limit + 1);
	query.with(new Sort(new Order(Sort.Direction.ASC, IAViewUpdate.FIELD_CREATIONDATE)));
	return mongoTemplate.find(query, IAViewUpdate.class);
    }

}

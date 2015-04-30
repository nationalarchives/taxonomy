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
    public List<IAViewUpdate> findDocumentsCreatedAfterDateAndCreatedBeforeDate(Date gtDate, Date ltDate, Integer limit) {
	List<Criteria> listOfCriterias = new ArrayList<Criteria>();
	if (gtDate != null) {
	    listOfCriterias.add(Criteria.where(IAViewUpdate.FIELD_CREATIONDATE).gt(gtDate));
	}
	if (ltDate != null) {
	    listOfCriterias.add(Criteria.where(IAViewUpdate.FIELD_CREATIONDATE).lt(ltDate));
	}
	Query query = new Query(new Criteria().andOperator(listOfCriterias.toArray(new Criteria[0])));

	query.limit(limit + 1);
	query.with(new Sort(new Order(Sort.Direction.ASC, IAViewUpdate.FIELD_CREATIONDATE), new Order(
		Sort.Direction.ASC, IAViewUpdate.FIELD_DOCREFERENCE)));
	return mongoTemplate.find(query, IAViewUpdate.class);
    }

    @Override
    public List<IAViewUpdate> findDocumentsCreatedAfterDocumentAndCreatedBeforeDate(IAViewUpdate afterIAViewUpdate,
	    Date ltDate, Integer limit) {
	List<Criteria> listOfCriterias = new ArrayList<Criteria>();
	listOfCriterias.add(new Criteria().orOperator(
		Criteria.where(IAViewUpdate.FIELD_CREATIONDATE).gt(afterIAViewUpdate.getCreationDate()),
		new Criteria().andOperator(
			Criteria.where(IAViewUpdate.FIELD_CREATIONDATE).gte(afterIAViewUpdate.getCreationDate()),
			Criteria.where(IAViewUpdate.FIELD_DOCREFERENCE).gt(afterIAViewUpdate.getDocReference()))));
	if (ltDate != null) {
	    listOfCriterias.add(Criteria.where(IAViewUpdate.FIELD_CREATIONDATE).lt(ltDate));
	}
	Query query = new Query(new Criteria().andOperator(listOfCriterias.toArray(new Criteria[0])));

	query.limit(limit + 1);
	query.with(new Sort(new Order(Sort.Direction.ASC, IAViewUpdate.FIELD_CREATIONDATE), new Order(
		Sort.Direction.ASC, IAViewUpdate.FIELD_DOCREFERENCE)));
	return mongoTemplate.find(query, IAViewUpdate.class);
    }

    @Override
    public void findAndRemoveByDocReference(String docReference) {
	Query query = new Query(Criteria.where(IAViewUpdate.FIELD_DOCREFERENCE).is(docReference));
	mongoTemplate.remove(query, IAViewUpdate.class);
    }

}

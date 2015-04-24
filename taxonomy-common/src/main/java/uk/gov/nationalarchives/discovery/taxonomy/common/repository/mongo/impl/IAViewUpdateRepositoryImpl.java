package uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.impl;

import java.util.Calendar;
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
    public List<IAViewUpdate> findWhereMoreRecentThanDocumentAndUntil5SecondsInPast(IAViewUpdate lastIAViewUpdate,
	    Integer limit) {
	// FIXME to ensure there is no data lost when commiting from several
	// servers that might have different clocks, I came up with this
	// solution. Not ideal though.
	Calendar instance = Calendar.getInstance();
	instance.add(Calendar.SECOND, -5);
	Date nowMinus2seconds = instance.getTime();

	Query query = new Query(new Criteria().andOperator(
		Criteria.where(IAViewUpdate.FIELD_CREATIONDATE).gte(lastIAViewUpdate.getCreationDate()), Criteria
			.where(IAViewUpdate.FIELD_CREATIONDATE).lt(nowMinus2seconds)));
	
	query.limit(limit + 1);
	query.with(new Sort(new Order(Sort.Direction.ASC, IAViewUpdate.FIELD_CREATIONDATE)));
	List<IAViewUpdate> listOfIAViewUpdatesToProcess = mongoTemplate.find(query, IAViewUpdate.class);
	// FIXME findByDocumentEqualOrMoreRecentThan: this way to browse the
	// collection is wrong but does the job. Results in some documents being
	// updates twice
	removeLastIaViewUpdateFromList(lastIAViewUpdate, listOfIAViewUpdatesToProcess);
	return listOfIAViewUpdatesToProcess;
    }

    private void removeLastIaViewUpdateFromList(IAViewUpdate lastIAViewUpdate,
	    List<IAViewUpdate> listOfIAViewUpdatesToProcess) {
	for (IAViewUpdate iaViewUpdate : listOfIAViewUpdatesToProcess) {
	    if (iaViewUpdate.getDocReference().equals(lastIAViewUpdate.getDocReference())) {
		listOfIAViewUpdatesToProcess.remove(iaViewUpdate);
	    }
	    break;
	}

    }

}

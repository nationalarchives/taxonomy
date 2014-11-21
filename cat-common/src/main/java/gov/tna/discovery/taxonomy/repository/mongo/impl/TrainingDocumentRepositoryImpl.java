package gov.tna.discovery.taxonomy.repository.mongo.impl;

import gov.tna.discovery.taxonomy.repository.domain.TrainingDocument;
import gov.tna.discovery.taxonomy.repository.mongo.TrainingDocumentRepositoryCustom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class TrainingDocumentRepositoryImpl implements TrainingDocumentRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void deleteByCategory(String categoryName) {
	Query query = new Query(Criteria.where("CATEGORY").is(categoryName));
	mongoTemplate.remove(query, TrainingDocument.class);
    }

}

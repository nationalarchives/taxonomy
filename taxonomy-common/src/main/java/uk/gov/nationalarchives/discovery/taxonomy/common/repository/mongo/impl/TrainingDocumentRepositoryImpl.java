package uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.impl;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.TrainingDocument;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.TrainingDocumentRepositoryCustom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.mongodb.WriteResult;

@Repository
@ConditionalOnProperty(prefix = "lucene.", value = "loadTSetServiceLayer")
public class TrainingDocumentRepositoryImpl implements TrainingDocumentRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public int deleteByCategory(String categoryName) {
	Query query = new Query(Criteria.where("CATEGORY").is(categoryName));
	WriteResult writeResult = mongoTemplate.remove(query, TrainingDocument.class);
	return writeResult.getN();
    }

}

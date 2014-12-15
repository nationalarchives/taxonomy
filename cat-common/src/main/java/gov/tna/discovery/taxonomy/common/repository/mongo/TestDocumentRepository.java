package gov.tna.discovery.taxonomy.common.repository.mongo;

import gov.tna.discovery.taxonomy.common.repository.domain.mongo.TestDocument;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestDocumentRepository extends CrudRepository<TestDocument, String> {

}

package uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.TestDocument;

import org.springframework.data.repository.CrudRepository;

public interface TestDocumentRepository extends CrudRepository<TestDocument, String> {

}

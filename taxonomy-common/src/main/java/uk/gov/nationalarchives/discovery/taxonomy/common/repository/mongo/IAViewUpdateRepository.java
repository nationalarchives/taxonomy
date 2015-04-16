package uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo;

import org.springframework.data.repository.CrudRepository;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.IAViewUpdate;

public interface IAViewUpdateRepository extends CrudRepository<IAViewUpdate, String>, IAViewUpdateRepositoryCustom {

}

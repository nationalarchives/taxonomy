package gov.tna.discovery.taxonomy.common.repository.mongo;

import gov.tna.discovery.taxonomy.common.domain.repository.mongo.IAViewUpdate;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IAViewUpdateRepository extends CrudRepository<IAViewUpdate, String> {

}

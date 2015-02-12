package gov.tna.discovery.taxonomy.common.repository.mongo;

import gov.tna.discovery.taxonomy.common.domain.repository.mongo.Category;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends CrudRepository<Category, String> {

    public Category findByCiaid(String ciaid);

    public Category findByTtl(String ttl);
}

package gov.tna.discovery.taxonomy.common.repository.mongo;

import gov.tna.discovery.taxonomy.common.repository.domain.mongo.Category;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

//TODO index relevant fields
@Repository
public interface CategoryRepository extends CrudRepository<Category, String> {

    public Category findByCiaid(String ciaid);
}

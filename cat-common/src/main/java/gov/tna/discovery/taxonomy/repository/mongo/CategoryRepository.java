package gov.tna.discovery.taxonomy.repository.mongo;

import gov.tna.discovery.taxonomy.repository.domain.mongo.Category;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends CrudRepository<Category,Long>, CategoryRepositoryCustom{

}

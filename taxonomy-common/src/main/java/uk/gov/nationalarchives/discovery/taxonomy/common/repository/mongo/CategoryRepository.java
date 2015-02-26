package uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.Category;

/**
 * Repository dedicated to categories<br/>
 * 
 * @author jcharlet
 *
 */
public interface CategoryRepository {

    /**
     * Find by identifier
     * 
     * @param ciaid
     * @return
     */
    public Category findByCiaid(String ciaid);

    /**
     * find by title
     * 
     * @param ttl
     * @return
     */
    public Category findByTtl(String ttl);

    /**
     * count number of elements in collection
     * 
     * @return
     */
    public Long count();

    /**
     * retrieve all elements from collection
     * 
     * @return
     */
    public Iterable<Category> findAll();

    /**
     * save new category
     * 
     * @param category
     */
    public void save(Category category);
}

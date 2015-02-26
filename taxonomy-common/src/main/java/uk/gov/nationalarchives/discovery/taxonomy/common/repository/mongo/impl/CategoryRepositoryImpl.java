/**
 * 
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo.Category;
import uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.CategoryRepository;

/**
 * Implementation of CategoryRepository using MongoTemplate<br/>
 * The reason is that Spring does not allow to use separate database names or
 * locations while the categories collection is on a mongo database separate
 * from other collections. So we cannot use MongoRepositories here and have to
 * rewrite everything
 * 
 * @author jcharlet
 *
 */
@Repository
public class CategoryRepositoryImpl implements CategoryRepository {

    private static final String TTL_FIELD = "ttl";
    private static final String CIAID_FIELD = "ciaid";
    private final MongoTemplate categoriesMongoTemplate;

    @Autowired
    public CategoryRepositoryImpl(MongoTemplate categoriesMongoTemplate) {
	super();
	this.categoriesMongoTemplate = categoriesMongoTemplate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.
     * CategoryRepository#findByCiaid(java.lang.String)
     */
    @Override
    public Category findByCiaid(String ciaid) {
	return categoriesMongoTemplate.findOne(new Query(Criteria.where(CIAID_FIELD).is(ciaid)), Category.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.
     * CategoryRepository#findByTtl(java.lang.String)
     */
    @Override
    public Category findByTtl(String ttl) {
	return categoriesMongoTemplate.findOne(new Query(Criteria.where(TTL_FIELD).is(ttl)), Category.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.
     * CategoryRepository#count()
     */
    @Override
    public Long count() {
	return categoriesMongoTemplate.count(new Query(), Category.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.
     * CategoryRepository#findAll()
     */
    @Override
    public Iterable<Category> findAll() {
	return categoriesMongoTemplate.findAll(Category.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see uk.gov.nationalarchives.discovery.taxonomy.common.repository.mongo.
     * CategoryRepository
     * #save(uk.gov.nationalarchives.discovery.taxonomy.common.
     * domain.repository.mongo.Category)
     */
    @Override
    public void save(Category category) {
	categoriesMongoTemplate.save(category);
    }

}

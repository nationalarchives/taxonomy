/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
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

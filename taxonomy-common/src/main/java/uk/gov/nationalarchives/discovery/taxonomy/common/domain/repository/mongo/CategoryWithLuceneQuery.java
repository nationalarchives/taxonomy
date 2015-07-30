/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo;

import java.io.Serializable;

import org.apache.lucene.search.Query;

/**
 * extends @Category and contains the parsed string query by Lucene parser
 * 
 * @author jcharlet
 *
 */
public class CategoryWithLuceneQuery extends Category implements Serializable {

    private static final long serialVersionUID = 1927440661373530509L;

    private transient Query parsedQry;

    public CategoryWithLuceneQuery(Category category, Query parsedQry) {
	super();
	this.set_id(category.get_id());
	this.setCiaid(category.getCiaid());
	this.setLck(category.getLck());
	this.setQry(category.getQry());
	this.setSc(category.getSc());
	this.setTtl(category.getTtl());
	this.parsedQry = parsedQry;
    }

    public Query getParsedQry() {
	return parsedQry;
    }

    public void setParsedQry(Query parsedQry) {
	this.parsedQry = parsedQry;
    }

}

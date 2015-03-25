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

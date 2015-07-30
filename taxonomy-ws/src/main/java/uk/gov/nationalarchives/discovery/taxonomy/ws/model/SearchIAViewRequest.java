/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.ws.model;

/**
 * request object for a search on Taxonomy WS
 * 
 * @author jcharlet
 *
 */
public class SearchIAViewRequest {
    private String categoryQuery;

    private Double score;

    private Integer offset;

    private Integer limit;

    public String getCategoryQuery() {
	return categoryQuery;
    }

    public void setCategoryQuery(String categoryQuery) {
	this.categoryQuery = categoryQuery;
    }

    public Double getScore() {
	return score;
    }

    public void setScore(Double score) {
	this.score = score;
    }

    public Integer getOffset() {
	return offset;
    }

    public void setOffset(Integer offset) {
	this.offset = offset;
    }

    public Integer getLimit() {
	return limit;
    }

    public void setLimit(Integer limit) {
	this.limit = limit;
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("SearchIAViewRequest [categoryQuery=");
	builder.append(categoryQuery);
	builder.append(", score=");
	builder.append(score);
	builder.append(", offset=");
	builder.append(offset);
	builder.append(", limit=");
	builder.append(limit);
	builder.append("]");
	return builder.toString();
    }

}

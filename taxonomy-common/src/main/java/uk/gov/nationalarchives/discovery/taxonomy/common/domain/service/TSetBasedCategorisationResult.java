/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.domain.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

/**
 * Details on one result of categorisation based on training set<br/>
 * returns
 * <ul>
 * <li>name of a category found</li>
 * <li>total score for that category</li>
 * <li>number of documents taken into account for that category</li>
 * </ul>
 * 
 * @author jcharlet
 *
 */
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class TSetBasedCategorisationResult extends CategorisationResult {

    @JsonProperty
    private Integer numberOfFoundDocuments;

    public TSetBasedCategorisationResult(String name, Float score, Integer numberOfFoundDocuments) {
	super();
	this.name = name;
	this.score = score;
	this.numberOfFoundDocuments = numberOfFoundDocuments;
    }

    public Integer getNumberOfFoundDocuments() {
	return numberOfFoundDocuments;
    }

    public void setNumberOfFoundDocuments(Integer numberOfFoundDocuments) {
	this.numberOfFoundDocuments = numberOfFoundDocuments;
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("CategorisationResult [name=");
	builder.append(name);
	builder.append(", score=");
	builder.append(score);
	builder.append(", numberOfFoundDocuments=");
	builder.append(numberOfFoundDocuments);
	builder.append("]");
	return builder.toString();
    }

}
/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.domain.service.legacy;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({ "searchResult" })
public class LegacySearchResponse {

    @JsonProperty("searchResult")
    private SearchResult searchResult;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return The searchResult
     */
    @JsonProperty("searchResult")
    public SearchResult getSearchResult() {
	return searchResult;
    }

    /**
     * 
     * @param searchResult
     *            The searchResult
     */
    @JsonProperty("searchResult")
    public void setSearchResult(SearchResult searchResult) {
	this.searchResult = searchResult;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
	return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
	this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("LegacySearchResponse [searchResult=");
	builder.append(searchResult);
	builder.append(", additionalProperties=");
	builder.append(additionalProperties);
	builder.append("]");
	return builder.toString();
    }

}

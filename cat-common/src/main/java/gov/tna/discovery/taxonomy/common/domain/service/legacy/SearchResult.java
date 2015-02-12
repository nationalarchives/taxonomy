package gov.tna.discovery.taxonomy.common.domain.service.legacy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
@JsonPropertyOrder({ "TotalResults", "SearchResultList" })
public class SearchResult {

    @JsonProperty("TotalResults")
    private Integer TotalResults;
    @JsonProperty("SearchResultList")
    private List<gov.tna.discovery.taxonomy.common.domain.service.legacy.SearchResultList> SearchResultList = new ArrayList<gov.tna.discovery.taxonomy.common.domain.service.legacy.SearchResultList>();
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return The TotalResults
     */
    @JsonProperty("TotalResults")
    public Integer getTotalResults() {
	return TotalResults;
    }

    /**
     * 
     * @param TotalResults
     *            The TotalResults
     */
    @JsonProperty("TotalResults")
    public void setTotalResults(Integer TotalResults) {
	this.TotalResults = TotalResults;
    }

    /**
     * 
     * @return The SearchResultList
     */
    @JsonProperty("SearchResultList")
    public List<gov.tna.discovery.taxonomy.common.domain.service.legacy.SearchResultList> getSearchResultList() {
	return SearchResultList;
    }

    /**
     * 
     * @param SearchResultList
     *            The SearchResultList
     */
    @JsonProperty("SearchResultList")
    public void setSearchResultList(
	    List<gov.tna.discovery.taxonomy.common.domain.service.legacy.SearchResultList> SearchResultList) {
	this.SearchResultList = SearchResultList;
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
	builder.append("SearchResult [TotalResults=");
	builder.append(TotalResults);
	builder.append(", SearchResultList=");
	builder.append(SearchResultList);
	builder.append(", additionalProperties=");
	builder.append(additionalProperties);
	builder.append("]");
	return builder.toString();
    }

}

package gov.tna.discovery.taxonomy.common.service.domain;

import java.util.List;

import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class PaginatedList<T> {
    private List<T> results;
    private Integer limit;
    private Integer offset;
    private Integer numberOfResults;
    private Double minimumScore;

    /**
     * Returns the current number of elements. <br/>
     * Null safe
     * 
     * @return
     */
    public int size() {
	if (!CollectionUtils.isEmpty(results)) {
	    return results.size();
	}
	return 0;
    }

    public PaginatedList() {
	super();
    }

    public PaginatedList(int limit, int offset, Double minimumScore) {
	super();
	this.limit = limit;
	this.offset = offset;
	this.minimumScore = minimumScore;
    }

    public List<T> getResults() {
	return results;
    }

    public void setResults(List<T> results) {
	this.results = results;
    }

    public Integer getNumberOfResults() {
	return numberOfResults;
    }

    public void setNumberOfResults(Integer numberOfResults) {
	this.numberOfResults = numberOfResults;
    }

    public Integer getLimit() {
	return limit;
    }

    public void setLimit(Integer limit) {
	this.limit = limit;
    }

    public Integer getOffset() {
	return offset;
    }

    public void setOffset(Integer offset) {
	this.offset = offset;
    }

    public Double getMinimumScore() {
	return minimumScore;
    }

    public void setMinimumScore(Double minimumScore) {
	this.minimumScore = minimumScore;
    }

}

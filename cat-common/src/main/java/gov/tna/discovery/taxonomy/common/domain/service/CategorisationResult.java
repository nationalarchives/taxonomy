package gov.tna.discovery.taxonomy.common.domain.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

/**
 * Details on one result of categorisation based on category queries<br/>
 * returns
 * <ul>
 * <li>name of a category found</li>
 * <li>total score for that category</li>
 * </ul>
 * 
 * @author jcharlet
 *
 */
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class CategorisationResult {
    @JsonProperty
    protected String name;
    @JsonProperty
    protected Float score;

    public CategorisationResult() {
	super();
    }

    public CategorisationResult(String name, Float score) {
	super();
	this.name = name;
	this.score = score;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public Float getScore() {
	return score;
    }

    public void setScore(Float score) {
	this.score = score;
    }
}

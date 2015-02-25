package uk.gov.nationalarchives.discovery.taxonomy.common.domain.service;

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
    protected String ciaid;
    @JsonProperty
    protected Float score;

    public CategorisationResult() {
	super();
    }

    public CategorisationResult(String name, String ciaid, Float score) {
	super();
	this.name = name;
	this.ciaid = ciaid;
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

    public String getCiaid() {
	return ciaid;
    }

    public void setCiaid(String ciaid) {
	this.ciaid = ciaid;
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("CategorisationResult [name=");
	builder.append(name);
	builder.append(", ciaid=");
	builder.append(ciaid);
	builder.append(", score=");
	builder.append(score);
	builder.append("]");
	return builder.toString();
    }

}

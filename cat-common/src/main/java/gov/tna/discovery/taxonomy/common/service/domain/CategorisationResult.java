package gov.tna.discovery.taxonomy.common.service.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

/**
 * Details on one result of categorisation<br/>
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
public class CategorisationResult {
    @JsonProperty
    private String name;
    @JsonProperty
    private Float score;
    @JsonProperty
    private Integer numberOfFoundDocuments;

    public CategorisationResult(String name, Float score, Integer numberOfFoundDocuments) {
	super();
	this.name = name;
	this.score = score;
	this.numberOfFoundDocuments = numberOfFoundDocuments;
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
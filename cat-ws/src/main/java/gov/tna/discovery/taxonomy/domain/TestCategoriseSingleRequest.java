package gov.tna.discovery.taxonomy.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TestCategoriseSingleRequest {

    private String title;

    @JsonProperty(required = true)
    private String description;

    private String contextDescription;

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public String getContextDescription() {
	return contextDescription;
    }

    public void setContextDescription(String contextDescription) {
	this.contextDescription = contextDescription;
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("TestCategoriseSingleRequest [title=");
	builder.append(title);
	builder.append(", description=");
	builder.append(description);
	builder.append(", contextDescription=");
	builder.append(contextDescription);
	builder.append("]");
	return builder.toString();
    }

}

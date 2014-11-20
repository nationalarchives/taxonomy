package gov.tna.discovery.taxonomy.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TestCategoriseSingleRequest {

    @JsonProperty(value = "TITLE")
    private String title;

    @JsonProperty(value = "DESCRIPTION", required = true)
    private String description;

    @JsonProperty(value = "CONTEXTDESCRIPTION")
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

}

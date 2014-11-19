package gov.tna.discovery.taxonomy.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PublishRequest {
    @JsonProperty(value = "CIAID")
    private String ciaid;

    public PublishRequest(String ciaid) {
	super();
	this.ciaid = ciaid;
    }

    public PublishRequest() {
	super();
    }

    public String getCiaid() {
	return ciaid;
    }

    public void setCiaid(String ciaid) {
	this.ciaid = ciaid;
    }
}

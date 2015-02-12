package gov.tna.discovery.taxonomy.ws.model;

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

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("PublishRequest [ciaid=");
	builder.append(ciaid);
	builder.append("]");
	return builder.toString();
    }

}

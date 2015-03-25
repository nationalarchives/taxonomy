package uk.gov.nationalarchives.discovery.taxonomy.ws.model;

import uk.gov.nationalarchives.discovery.taxonomy.common.domain.exception.TaxonomyErrorType;

public class TaxonomyErrorResponse {
    private TaxonomyErrorType error;
    private String message;

    public TaxonomyErrorResponse() {
	super();
    }

    public TaxonomyErrorResponse(TaxonomyErrorType error, String message) {
	super();
	this.error = error;
	this.message = message;
    }

    public TaxonomyErrorType getError() {
	return error;
    }

    public void setError(TaxonomyErrorType error) {
	this.error = error;
    }

    public String getMessage() {
	return message;
    }

    public void setMessage(String message) {
	this.message = message;
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("TaxonomyErrorResponse [error=");
	builder.append(error);
	builder.append(", message=");
	builder.append(message);
	builder.append("]");
	return builder.toString();
    }

}

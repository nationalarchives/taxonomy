package gov.tna.discovery.taxonomy.domain;

import gov.tna.discovery.taxonomy.service.exception.TaxonomyErrorType;

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

}

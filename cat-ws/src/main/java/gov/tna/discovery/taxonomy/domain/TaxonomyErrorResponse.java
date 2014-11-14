package gov.tna.discovery.taxonomy.domain;

public class TaxonomyErrorResponse {
    private TaxonomyErrorType errorType;
    private String errorMessage;

    public TaxonomyErrorResponse() {
	super();
    }

    public TaxonomyErrorResponse(TaxonomyErrorType errorType, String errorMessage) {
	super();
	this.errorType = errorType;
	this.errorMessage = errorMessage;
    }

    public TaxonomyErrorType getErrorType() {
	return errorType;
    }

    public void setErrorType(TaxonomyErrorType errorType) {
	this.errorType = errorType;
    }

    public String getErrorMessage() {
	return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
	this.errorMessage = errorMessage;
    }

}

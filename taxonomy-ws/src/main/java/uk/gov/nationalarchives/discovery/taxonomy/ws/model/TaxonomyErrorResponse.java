/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
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

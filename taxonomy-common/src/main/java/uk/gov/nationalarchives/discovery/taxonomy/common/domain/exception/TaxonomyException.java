/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.domain.exception;

public class TaxonomyException extends RuntimeException {
    private static final long serialVersionUID = -1431951861058942606L;

    private TaxonomyErrorType taxonomyErrorType;

    public TaxonomyException() {
	super();
    }

    public TaxonomyException(TaxonomyErrorType taxonomyErrorType) {
	super();
	this.taxonomyErrorType = taxonomyErrorType;
    }

    public TaxonomyException(TaxonomyErrorType taxonomyErrorType, String message) {
	super(message);
	this.taxonomyErrorType = taxonomyErrorType;
    }

    public TaxonomyException(TaxonomyErrorType taxonomyErrorType, Throwable cause) {
	super(cause);
	this.taxonomyErrorType = taxonomyErrorType;
    }

    public TaxonomyErrorType getTaxonomyErrorType() {
	return taxonomyErrorType;
    }

    public void setTaxonomyErrorType(TaxonomyErrorType taxonomyErrorType) {
	this.taxonomyErrorType = taxonomyErrorType;
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("TaxonomyException [taxonomyErrorType=");
	builder.append(taxonomyErrorType);
	builder.append(", getMessage()=");
	builder.append(getMessage());
	builder.append("]");
	return builder.toString();
    }

}

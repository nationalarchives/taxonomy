/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.ws.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request object for the publish method on Taxonomy WS
 * 
 * @author jcharlet
 *
 */
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

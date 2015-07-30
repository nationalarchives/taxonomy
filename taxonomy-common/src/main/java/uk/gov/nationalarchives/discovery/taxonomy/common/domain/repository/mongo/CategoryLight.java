/** 
 * Copyright (c) 2015, The National Archives
 * http://www.nationalarchives.gov.uk 
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public 
 * License, v. 2.0. If a copy of the MPL was not distributed with this 
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo;

public class CategoryLight {
    private String ciaid;
    private String ttl;

    public CategoryLight(String ciaid, String ttl) {
	super();
	this.ciaid = ciaid;
	this.ttl = ttl;
    }

    public String getCiaid() {
	return ciaid;
    }

    public void setCiaid(String ciaid) {
	this.ciaid = ciaid;
    }

    public String getTtl() {
	return ttl;
    }

    public void setTtl(String ttl) {
	this.ttl = ttl;
    }

    public String getCiaidAndTtl() {
	return new StringBuilder(this.ciaid).append(" ").append(this.ttl).toString();
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("CategoryLight [ciaid=");
	builder.append(ciaid);
	builder.append(", ttl=");
	builder.append(ttl);
	builder.append("]");
	return builder.toString();
    }

}

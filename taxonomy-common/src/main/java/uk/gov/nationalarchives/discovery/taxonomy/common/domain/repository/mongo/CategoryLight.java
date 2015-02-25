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

package gov.tna.discovery.taxonomy.common.domain.repository.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "categories")
public class Category {
    @Id
    private String _id;
    @Field(value = "CIAID")
    private String ciaid;
    private String qry;
    private String ttl;
    @Field(value = "SC")
    private Double sc;
    private Boolean lck;

    public String get_id() {
	return _id;
    }

    public void set_id(String _id) {
	this._id = _id;
    }

    public String getQry() {
	return qry;
    }

    public void setQry(String qry) {
	this.qry = qry;
    }

    public String getTtl() {
	return ttl;
    }

    public void setTtl(String ttl) {
	this.ttl = ttl;
    }

    public Boolean getLck() {
	return lck;
    }

    public void setLck(Boolean lck) {
	this.lck = lck;
    }

    public String getCiaid() {
	return ciaid;
    }

    public void setCiaid(String ciaid) {
	this.ciaid = ciaid;
    }

    public Double getSc() {
	return sc;
    }

    public void setSc(Double sc) {
	this.sc = sc;
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("Category [_id=");
	builder.append(_id);
	builder.append(", ciaid=");
	builder.append(ciaid);
	builder.append(", qry=");
	builder.append(qry);
	builder.append(", ttl=");
	builder.append(ttl);
	builder.append(", sc=");
	builder.append(sc);
	builder.append(", lck=");
	builder.append(lck);
	builder.append("]");
	return builder.toString();
    }

}

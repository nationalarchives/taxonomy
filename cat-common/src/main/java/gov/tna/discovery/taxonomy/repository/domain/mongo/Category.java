package gov.tna.discovery.taxonomy.repository.domain.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="categories")
public class Category {
    @Id
    private String _id;
    private String CIAID;
    private String qry;
    private String ttl;
    private Float SC;
    public String get_id() {
        return _id;
    }
    public void set_id(String _id) {
        this._id = _id;
    }
    public String getCIAID() {
        return CIAID;
    }
    public void setCIAID(String cIAID) {
        CIAID = cIAID;
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
    public Float getSC() {
        return SC;
    }
    public void setSC(Float sC) {
        SC = sC;
    }
    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("Category [_id=");
	builder.append(_id);
	builder.append(", CIAID=");
	builder.append(CIAID);
	builder.append(", qry=");
	builder.append(qry);
	builder.append(", ttl=");
	builder.append(ttl);
	builder.append(", SC=");
	builder.append(SC);
	builder.append("]");
	return builder.toString();
    }


}

package gov.tna.discovery.taxonomy.repository.domain;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="trainingset")
public class TrainingDocument {
    private String _id;
    private String TITLE;
    private String DESCRIPTION;
    private String CATEGORY;

    public String get_id() {
	return _id;
    }

    public void set_id(String _id) {
	this._id = _id;
    }

    public String getTITLE() {
	return TITLE;
    }

    public void setTITLE(String tITLE) {
	TITLE = tITLE;
    }

    public String getDESCRIPTION() {
	return DESCRIPTION;
    }

    public void setDESCRIPTION(String dESCRIPTION) {
	DESCRIPTION = dESCRIPTION;
    }

    public String getCATEGORY() {
	return CATEGORY;
    }

    public void setCATEGORY(String cATEGORY) {
	CATEGORY = cATEGORY;
    }

}

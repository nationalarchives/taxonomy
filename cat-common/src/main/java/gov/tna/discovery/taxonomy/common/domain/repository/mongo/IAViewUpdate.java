package gov.tna.discovery.taxonomy.common.domain.repository.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "iaViewUpdates")
public class IAViewUpdate {

    @Id
    private long timestamp;

    private String docReference;

    private String[] categories;
    private String catDocRef;

    public IAViewUpdate(long timestamp) {
	super();
	this.timestamp = timestamp;
    }

    public String getDocReference() {
	return docReference;
    }

    public void setDocReference(String docReference) {
	this.docReference = docReference;
    }

    public String[] getCategories() {
	return categories;
    }

    public void setCategories(String[] categories) {
	this.categories = categories;
    }

    public String getCatDocRef() {
	return catDocRef;
    }

    public void setCatDocRef(String catDocRef) {
	this.catDocRef = catDocRef;
    }

    public long getTimestamp() {
	return timestamp;
    }

    public void setTimestamp(long timestamp) {
	this.timestamp = timestamp;
    }

}

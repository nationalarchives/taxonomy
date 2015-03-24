package uk.gov.nationalarchives.discovery.taxonomy.common.domain.repository.mongo;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * IAView with its categories <br/>
 * this collection contains all the results of categorisation for all documents.
 * Would potentially contain several update documents per IAView.
 * 
 * @author jcharlet
 *
 */
@Document(collection = "iaViewUpdates")
public class IAViewUpdate {

    public static final String ID_FIELDNAME = "id";

    @Id
    private ObjectId id;

    private Date creationDate;

    private String docReference;

    private List<CategoryLight> categories;
    private String catDocRef;

    public IAViewUpdate() {
	super();
    }

    public String getDocReference() {
	return docReference;
    }

    public void setDocReference(String docReference) {
	this.docReference = docReference;
    }

    public List<CategoryLight> getCategories() {
	return categories;
    }

    public void setCategories(List<CategoryLight> categories) {
	this.categories = categories;
    }

    public String getCatDocRef() {
	return catDocRef;
    }

    public void setCatDocRef(String catDocRef) {
	this.catDocRef = catDocRef;
    }

    public ObjectId getId() {
	return id;
    }

    public void setId(ObjectId id) {
	this.id = id;
    }

    public Date getCreationDate() {
	return creationDate;
    }

    public void setCreationDate(Date creationDate) {
	this.creationDate = creationDate;
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("IAViewUpdate [creationDate=");
	builder.append(creationDate);
	builder.append(", docReference=");
	builder.append(docReference);
	builder.append("]");
	return builder.toString();
    }

}

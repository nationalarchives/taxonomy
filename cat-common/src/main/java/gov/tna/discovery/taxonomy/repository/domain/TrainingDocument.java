package gov.tna.discovery.taxonomy.repository.domain;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "trainingset")
public class TrainingDocument {
    private String _id;
    @Field(value = "TITLE")
    private String title;
    @Field(value = "DESCRIPTION")
    private String description;

    @Field(value = "CATEGORY")
    private String category;

    @Field(value = "DOCREFERENCE")
    private String docReference;

    public String get_id() {
	return _id;
    }

    public void set_id(String _id) {
	this._id = _id;
    }

    public String getTitle() {
	return title;
    }

    public void setTitle(String title) {
	this.title = title;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public String getCategory() {
	return category;
    }

    public void setCategory(String category) {
	this.category = category;
    }

    public String getDocReference() {
	return docReference;
    }

    public void setDocReference(String docReference) {
	this.docReference = docReference;
    }

}

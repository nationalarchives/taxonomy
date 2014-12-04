package gov.tna.discovery.taxonomy.ws.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class TestCategoriseSingleRequest {

    private String title;

    @JsonProperty(required = true)
    private String description;

    private String contextDescription;

    private String docReference;
    private String catDocRef;
    private String[] corpBodys;
    private String[] subjects;
    @JsonProperty(value = "placeName")
    private String[] place_NAME;
    @JsonProperty(value = "personFullName")
    private String[] person_FULLNAME;
    private String coveringDates;

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

    public String getContextDescription() {
	return contextDescription;
    }

    public void setContextDescription(String contextDescription) {
	this.contextDescription = contextDescription;
    }

    public String getDocReference() {
	return docReference;
    }

    public void setDocReference(String docReference) {
	this.docReference = docReference;
    }

    public String getCatDocRef() {
	return catDocRef;
    }

    public void setCatDocRef(String catDocRef) {
	this.catDocRef = catDocRef;
    }

    public String[] getCorpBodys() {
	return corpBodys;
    }

    public void setCorpBodys(String[] corpBodys) {
	this.corpBodys = corpBodys;
    }

    public String[] getSubjects() {
	return subjects;
    }

    public void setSubjects(String[] subjects) {
	this.subjects = subjects;
    }

    public String[] getPlace_NAME() {
	return place_NAME;
    }

    public void setPlace_NAME(String[] place_NAME) {
	this.place_NAME = place_NAME;
    }

    public String[] getPerson_FULLNAME() {
	return person_FULLNAME;
    }

    public void setPerson_FULLNAME(String[] person_FULLNAME) {
	this.person_FULLNAME = person_FULLNAME;
    }

    public String getCoveringDates() {
	return coveringDates;
    }

    public void setCoveringDates(String coveringDates) {
	this.coveringDates = coveringDates;
    }

    @Override
    public String toString() {
	StringBuilder builder = new StringBuilder();
	builder.append("TestCategoriseSingleRequest [title=");
	builder.append(title);
	builder.append(", description=");
	builder.append(description);
	builder.append(", contextDescription=");
	builder.append(contextDescription);
	builder.append("]");
	return builder.toString();
    }

}
